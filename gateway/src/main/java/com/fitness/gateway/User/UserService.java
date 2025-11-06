package com.fitness.gateway.User;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final WebClient userServiceWebClient;

    public Mono<Boolean> validateUser(String userId) {

        return userServiceWebClient.get()
                .uri("/api/users/{userId}/validate", userId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume(WebClientResponseException.class, ex -> {
                    if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                        return Mono.error(new RuntimeException("User not found in User Service: " + userId));
                    } else if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                        return Mono.error(new RuntimeException("Bad request sent to User Service for user: " + userId));
                    }
                    return Mono.error(new RuntimeException("Error occurred while validating user: " + userId));
                });
    }

	public Mono<UserResponse> registerUser(RegisterRequest registerRequest) {
		log.info("Calling user service to register user: {}", registerRequest);
		return userServiceWebClient.post()
				.uri("/api/users/register")
				.bodyValue(registerRequest)
				.retrieve()
				.bodyToMono(UserResponse.class)
				.onErrorResume(WebClientResponseException.class, e -> {
                    if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                        return Mono.error(new RuntimeException("Bad request: " + e.getMessage()));
                    }
                    return Mono.error(new RuntimeException("Error occurred while validating user: " + e.getMessage()));
                });
	}
}
