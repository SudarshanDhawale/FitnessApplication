package com.fitness.activityservice.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserValidationService {
	
	private final WebClient userServiceWebClient;
	
	public boolean validateUser(String userId) {
	    return userServiceWebClient.get()
	            .uri("/api/users/{userId}/validate", userId)
	            .retrieve()
	            .onStatus(status -> status.is4xxClientError(), response ->
	                response.bodyToMono(String.class)
	                        .map(body -> new RuntimeException("User not found: " + body))
	            )
	            .onStatus(status -> status.is5xxServerError(), response ->
	                response.bodyToMono(String.class)
	                        .map(body -> new RuntimeException("UserService error: " + body))
	            )
	            .bodyToMono(Boolean.class)
	            .block();
	}

}
