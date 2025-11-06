package com.fitness.aiservice.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityAiService {
	
	private final GeminiService geminiService;
	
	public Recommendation generateRecommendation(Activity activity) {
		String prompt = createPromptForActivity(activity);
		String aiResponse = geminiService.getRecommendation(prompt);
		
		return processAiResponse(activity, aiResponse);
	}

	private Recommendation processAiResponse(Activity activity, String aiResponse) {		
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.readTree(aiResponse);
			JsonNode textNode = rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text");
			
			String jsonContent = textNode.asText()
                    .replaceAll("```json\\n", "")
                    .replaceAll("\\n```", "")
                    .trim();
			
			JsonNode analysisJson = mapper.readTree(jsonContent);
			JsonNode analysisNode = analysisJson.path("analysis");
			  
			StringBuilder fullAnalysis = new StringBuilder();
			
			addAnalysisSection(fullAnalysis, analysisNode, "overall", "Overall: ");
			addAnalysisSection(fullAnalysis, analysisNode, "pace", "Pace: ");
			addAnalysisSection(fullAnalysis, analysisNode, "heartRate", "Heart Rate: ");
			addAnalysisSection(fullAnalysis, analysisNode, "caloriesBurned", "Calories Burned: ");
			
			List<String> improvements = extractImprovements(analysisJson.path("improvements"));
			List<String> suggestions = extractsuggestions(analysisJson.path("suggestions"));
			List<String> safety = extractSafetyGuidelines(analysisJson.path("safety"));
			
			return Recommendation.builder()
					.activityId(activity.getId())
					.userId(activity.getUserId())
					.type(activity.getType().toString())
					.recommendation(fullAnalysis.toString().trim())
					.improvements(improvements)
					.suggestions(suggestions)
					.safety(safety)
					.createdAt(LocalDateTime.now())
					.build();
			
		} catch (Exception e) {
			e.printStackTrace();
			return defaultRecommendation(activity);
		}
	}

	private Recommendation defaultRecommendation(Activity activity) {
		
		return Recommendation.builder()
					.activityId(activity.getId())
					.userId(activity.getUserId())
					.type(activity.getType().toString())
					.recommendation("Unable to generate detaileda analysis.")
					.improvements(Collections.singletonList("No improvements available"))
					.suggestions(Collections.singletonList("No suggestions available"))
					.safety(Collections.singletonList("Follow General safety guidelines"))
					.createdAt(LocalDateTime.now())
					.build();
	}

	private List<String> extractSafetyGuidelines(JsonNode safetyNode) {
		List<String> safetyGuidelines = new ArrayList<>();
		if(safetyNode.isArray()) {
			safetyNode.forEach(item -> safetyGuidelines.add(item.asText()));
		}
		return safetyGuidelines.isEmpty() ?
				Collections.singletonList("Follow General safetSy guidelines") : safetyGuidelines;
	}

	private List<String> extractsuggestions(JsonNode suggestionsNode) {
		List<String> suggestions = new ArrayList<>();
		
		if (suggestionsNode.isArray()) {
			suggestionsNode.forEach(suggestion -> {
			String workout = suggestion.path("workout").asText();
			String description = suggestion.path("description").asText();
			
			suggestions.add(String.format("%s: %s", workout, description));
		});
	}
		return suggestions.isEmpty() ?
			Collections.singletonList("No suggestions provided") : suggestions;
	}

	private List<String> extractImprovements(JsonNode improvementsNode) {
		
		List<String> improvements = new ArrayList();
		
		if (improvementsNode.isArray()) {
				improvementsNode.forEach(improvement -> {
				String area = improvement.path("area").asText();
				String recommendation = improvement.path("recommendation").asText();
				improvements.add(String.format("%s: %s", area, recommendation));
			});
		}
		return improvements.isEmpty() ?
				Collections.singletonList("No improvements provided") : improvements;
	}

	private void addAnalysisSection(StringBuilder fullAnalysis, JsonNode analysisNode, String key, String prefix) {
		if(!analysisNode.path(key).isMissingNode()) {
			fullAnalysis.append(prefix)
				.append(analysisNode.path(key).asText())
				.append("\n");
			
		}
	}

	private String createPromptForActivity(Activity activity) {
		return String.format("""
				Analyze this fitness activity and provide detailed recommendations in the following EXACT JSON format:
				{
					"analysis": {
					  "overall": "Overall analysis here",
					  "pace": "Pace analysis here",
					  "heartRate": "Heart rate analysis here",
					  "caloriesBurned": "Calories burned analysis here"
					},
					
					"improvements": [
						{
						"area": "Area name",
						"recommendation": "Detailed recommendation"
						}
					],
					
					"suggestions": [
						{
						"workout": "workout name",
						"description": "detailed workout description" 
						}
					],
					
					"safety": [
						"Safety tip 1",
						"Safety tip 2"
					]
				}
				Analyse this acitivity:
				Activity Tyep: %s
				Duration (minutes): %d
				Calories Burned: %d
				Additional Metrics: %s
				
				Provide detailed analysis, focusing on performance,improvements, next workout, suggestions, and safety guidelines.
				Ensure the response is in valid JSON format as specified above.	
				""",
					activity.getType(),
					activity.getDuration(),
					activity.getCaloriesBurned(),
					activity.getAdditionalMetrics()
				);
	}
}
