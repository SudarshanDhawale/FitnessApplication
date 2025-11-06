package com.fitness.gateway.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
	
	@NotBlank(message = "Email is required")
	@Email(message = "Invalid e-mail Format")
	private String email;
	private String keycloakId;
	
	@NotBlank(message = "Password can't be empty")
	@Size(min = 6, message = "Password must contain atleast 6 characters")
	private String password;
	private String fname;
	private String lname;
}
