package com.fitness.userservice.services;

import org.springframework.stereotype.Service;

import com.fitness.userservice.UserRepository;
import com.fitness.userservice.dto.RegisterRequest;
import com.fitness.userservice.dto.UserResponse;
import com.fitness.userservice.exception.ResourceNotFoundException;
import com.fitness.userservice.models.User;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {
	
	private final UserRepository repository;
	
	public UserResponse register(RegisterRequest request) {
		
		if(repository.existsByEmail(request.getEmail())) {
			User existingUser = repository.findByEmail(request.getEmail());	
			
			UserResponse userResponse = new UserResponse();
			userResponse.setId(existingUser.getId());
			userResponse.setPassword(existingUser.getPassword());
			userResponse.setEmail(existingUser.getEmail());
			userResponse.setKeycloakId(existingUser.getKeycloakId());
			userResponse.setFname(existingUser.getFname());
			userResponse.setLname(existingUser.getLname());
			userResponse.setCreatedAt(existingUser.getCreatedAt());
			userResponse.setUpdatedAt(existingUser.getUpdatedAt());
			
			return userResponse;
		}
		
		User user = new User();
		user.setEmail(request.getEmail());
		user.setKeycloakId(request.getKeycloakId());
		user.setFname(request.getFname());
		user.setLname(request.getLname());
		user.setPassword(request.getPassword());
		
		//Saving user into database
		User savedUser = repository.save(user);
		
		UserResponse userResponse = new UserResponse();
		userResponse.setId(savedUser.getId());
		userResponse.setPassword(savedUser.getPassword());
		userResponse.setEmail(savedUser.getEmail());
		userResponse.setKeycloakId(savedUser.getKeycloakId());
		userResponse.setFname(savedUser.getFname());
		userResponse.setLname(savedUser.getLname());
		userResponse.setCreatedAt(savedUser.getCreatedAt());
		userResponse.setUpdatedAt(savedUser.getUpdatedAt());
		
		return userResponse;
	}

	public UserResponse getUserProfile(String userId) {
		
		User user = repository.findById(userId)
		        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
		
		UserResponse userResponse = new UserResponse();
		userResponse.setId(user.getId());
		userResponse.setPassword(user.getPassword());
		userResponse.setEmail(user.getEmail());
		userResponse.setFname(user.getFname());
		userResponse.setLname(user.getLname());
		userResponse.setCreatedAt(user.getCreatedAt());
		userResponse.setUpdatedAt(user.getUpdatedAt());
		return userResponse;
	}

	public Boolean existByUserId(String userId) {
		return repository.existsByKeycloakId(userId);
	}
}
