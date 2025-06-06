package com.movietix.userauthservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {
	private String email;
	private String userId;
	private String firstName;
	private String lastName;
	private String role;
	private String jwtToken;
}
