package com.fitness.aiservice.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fitness.aiservice.Repository.RecommendationRepository;
import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityMessageListener {

	private final ActivityAiService activityAiService;
	private final RecommendationRepository recommendationRepository;
	
	@KafkaListener(topics = "${kafka.topic.name}", groupId = "activity-processor-group")
	public void processActivityMessage(Activity activity) {
		log.info("Received activity for processing: {}", activity.getId());
		Recommendation recommendation = activityAiService.generateRecommendation(activity);
		recommendationRepository.save(recommendation);
		log.info("Saved recommendation with ID: {}", recommendation.getId());
	} 
}
