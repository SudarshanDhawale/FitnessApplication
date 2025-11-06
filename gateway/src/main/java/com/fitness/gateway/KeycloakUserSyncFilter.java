package com.fitness.gateway;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import com.fitness.gateway.User.RegisterRequest;
import com.fitness.gateway.User.UserService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class KeycloakUserSyncFilter implements WebFilter {
	private final UserService userService;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
		String token = exchange.getRequest().getHeaders().getFirst("Authorization");
		RegisterRequest registerRequest = getUserDetails(token);
		if (userId == null) {
			userId = registerRequest.getKeycloakId();
		}
		if (userId != null && token != null) {
			final String resolvedUserId = userId;
			return userService.validateUser(userId).flatMap(exists -> {
				if (!exists) {
					if (registerRequest != null) {
						return userService.registerUser(registerRequest).then(Mono.empty());
					} else {
						return Mono.empty();
					}
				} else {
					log.info("User with ID {} already exists in User Service", resolvedUserId);
					return Mono.empty();
				}
			}).then(Mono.defer(() -> {
				ServerHttpRequest mutatedRequest = exchange.getRequest().mutate().header("X-User-Id", resolvedUserId)
						.build();
				return chain.filter(exchange.mutate().request(mutatedRequest).build());
			}));
		}
		return chain.filter(exchange);
	}

	private RegisterRequest getUserDetails(String token) {
		try {
			String tokenWithoutBearer = token.replace("Bearer ", "").trim();
			SignedJWT signedJWT = SignedJWT.parse(tokenWithoutBearer);
			JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
			RegisterRequest request = new RegisterRequest();
			request.setEmail(claims.getStringClaim("email"));
			request.setKeycloakId(claims.getStringClaim("sub"));
			request.setFname(claims.getStringClaim("given_name"));
			request.setLname(claims.getStringClaim("family_name"));
			request.setPassword("dummy1234");
			return request;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}