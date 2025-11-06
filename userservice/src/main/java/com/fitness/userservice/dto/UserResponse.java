package com.fitness.userservice.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UserResponse {
	
	private String id;
	private String keycloakId;
	private String email;
	private String password;
	private String fname;
	private String lname;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
