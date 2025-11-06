package com.fitness.aiservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fitness.aiservice.Repository.RecommendationRepository;
import com.fitness.aiservice.model.Recommendation;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecommendationService {
	private final RecommendationRepository recommendationRepository;

	public List<Recommendation> getUserRecommendation(String userId) {
		
		return recommendationRepository.findByUserId(userId);
	}

	public Recommendation getActivityRecommendation(String activityId) {
		return recommendationRepository.findByActivityId(activityId)
				.orElseThrow(() -> new RuntimeException("No recommendation found for : " + activityId));
	}
}
