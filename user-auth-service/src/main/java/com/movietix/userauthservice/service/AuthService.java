package com.movietix.userauthservice.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;

import com.movietix.userauthservice.dto.ForgotPasswordRequest;
import com.movietix.userauthservice.dto.LoginRequest;
import com.movietix.userauthservice.dto.LoginResponse;
import com.movietix.userauthservice.dto.RegistrationRequest;
import com.movietix.userauthservice.dto.UpdatePasswordRequest;
import com.movietix.userauthservice.dto.ValidationDto;
import com.movietix.userauthservice.exceptions.EmailAlreadyExistsException;
import com.movietix.userauthservice.exceptions.ResourceNotFoundException;
import com.movietix.userauthservice.model.Role;
import com.movietix.userauthservice.model.SecretQuestion;
import com.movietix.userauthservice.model.User;
import com.movietix.userauthservice.repository.SecretQuestionRepository;
import com.movietix.userauthservice.repository.UserRepository;
import com.movietix.userauthservice.util.JwtProvider;

@Service
public class AuthService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private SecretQuestionRepository questionRepository;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private JwtProvider jwtProvider;

	@Transactional
	public void register(RegistrationRequest request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new EmailAlreadyExistsException("You email: " + request.getEmail() + " already exists");
		}
		SecretQuestion question = questionRepository.findById(request.getSecretQuestionId())
				.orElseThrow(() -> new ResourceAccessException(
						"No secret question found with id: " + request.getSecretQuestionId()));

		User user = User.builder().userId(generateUserId()).firstName(request.getFirstName())
				.lastName(request.getLastName()).email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword())).role(Role.CUSTOMER).secretQuestion(question)
				.answerToSecretQuestion(request.getAnswerToSecretQuestion()).build();

		userRepository.save(user);
	}

	@Transactional(readOnly = true)
	public LoginResponse login(LoginRequest request) {
		Authentication authenticate = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authenticate);
		User user = userRepository.findByEmail(request.getEmail()).orElse(null);
		String jwtToken = jwtProvider.generateToken(authenticate);
		return LoginResponse.builder().email(request.getEmail()).userId(user.getUserId()).firstName(user.getFirstName())
				.lastName(user.getLastName()).role(user.getRole().toString()).jwtToken(jwtToken).build();
	}

	@Transactional
	public void forgotPassword(String userid, ForgotPasswordRequest request) {
		User user = userRepository.findById(userid)
				.orElseThrow(() -> new ResourceNotFoundException("No user found with id: " + userid));
		if (user.getSecretQuestion().getId() != request.getSecurityQuestionId()
				|| !user.getAnswerToSecretQuestion().equals(request.getAnswer())) {
			throw new RuntimeException("Secret question or answer is incorrect");
		}
		user.setPassword(passwordEncoder.encode(request.getNewPassword()));
		userRepository.save(user);
	}

	@Transactional
	public void updatePassword(String jwtToken, UpdatePasswordRequest request) {
		if(validateAuthToken(jwtToken).isStatus()) {
			try {
				jwtToken = jwtToken.substring(7);
				String email = jwtProvider.extractSubject(jwtToken);
				User user = userRepository.findByEmail(email)
						.orElseThrow(() -> new ResourceNotFoundException("No user found with email: " + email));
				if (user.getSecretQuestion().getId() != request.getSecurityQuestionId()
						|| !user.getAnswerToSecretQuestion().equals(request.getAnswer())) {
					throw new RuntimeException("Secret question or answer is incorrect");
				}
				user.setPassword(passwordEncoder.encode(request.getNewPassword()));
				userRepository.save(user);
			}
			catch(Exception e) {
				throw e;
			}
		}
		else
			throw new RuntimeException("Invalid jwt token");
	}

	public ValidationDto validateAuthToken(String token) {
		ValidationDto response = new ValidationDto();
		try {
			token = token.substring(7);
			String email = jwtProvider.extractSubject(token);
			User user = userRepository.findByEmail(email)
					.orElseThrow(() -> new ResourceNotFoundException("No user found with email: " + email));
			response.setStatus(true);
			response.setUserId(user.getUserId());
			response.setRole(user.getRole().toString());
		} catch (Exception e) {
			response.setStatus(false);
		}
		return response;
	}

	private String generateUserId() {
		return "U" + UUID.randomUUID().toString();
	}

}
