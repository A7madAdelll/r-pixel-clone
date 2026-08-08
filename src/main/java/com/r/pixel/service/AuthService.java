package com.r.pixel.service;

import com.r.pixel.dto.AuthResponse;
import com.r.pixel.dto.LoginRequest;
import com.r.pixel.dto.RegisterRequest;
import com.r.pixel.entity.User;
import com.r.pixel.exception.ApiException;
import com.r.pixel.repository.UserRepository;
import com.r.pixel.security.JwtService;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
	}

	public AuthResponse register(RegisterRequest request) {
		String username = request.username() == null ? "" : request.username().trim();
		validateUsername(username);
		validatePassword(request.password());
		if (userRepository.findByUsername(username).isPresent()) {
			throw new ApiException(HttpStatus.CONFLICT, "Username is already taken");
		}
		User user = new User();
		user.setUsername(username);
		user.setPasswordHash(passwordEncoder.encode(request.password()));
		user.setCreatedAt(LocalDateTime.now());
		userRepository.save(user);
		return toResponse(user);
	}

	public AuthResponse login(LoginRequest request) {
		String username = request.username() == null ? "" : request.username().trim();
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));
		if (request.password() == null || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
			throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
		}
		return toResponse(user);
	}

	private AuthResponse toResponse(User user) {
		return new AuthResponse(jwtService.generateToken(user), user.getUsername());
	}

	private void validateUsername(String username) {
		if (username.isEmpty() || username.length() > 50) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Username must be 1-50 characters");
		}
		if (!username.matches("[a-zA-Z0-9_]+")) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Username may only contain letters, digits and underscores");
		}
	}

	private void validatePassword(String password) {
		if (password == null || password.length() < 6) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Password must be at least 6 characters");
		}
	}
}
