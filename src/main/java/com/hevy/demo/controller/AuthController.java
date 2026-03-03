package com.hevy.demo.controller;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hevy.demo.models.User;
import com.hevy.demo.repository.UserRepository;
import com.hevy.demo.service.JwtService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository,
            PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        if (request == null || isBlank(request.username) || isBlank(request.password)) {
            return ResponseEntity.badRequest().build();
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username, request.password));
        User user = (User) authentication.getPrincipal();
        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {
        if (request == null || isBlank(request.username) || isBlank(request.email) || isBlank(request.password)) {
            return ResponseEntity.badRequest().build();
        }

        Optional<User> existing = userRepository.findByUsername(request.username);
        if (existing.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        User user = new User();
        user.setUsername(request.username);
        user.setEmail(request.email);
        user.setPassword(passwordEncoder.encode(request.password));
        user.setFollowers(0);
        user.setFollowing(0);
        user.setWorkouts(0);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(null);
        user.setDeletedAt(null);
        user.setProfileImage(null);

        User saved = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(saved));
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public static class LoginRequest {
        public String username;
        public String password;
    }

    public static class RegisterRequest {
        public String username;
        public String email;
        public String password;
    }

    public static class LoginResponse {
        public String token;

        public LoginResponse(String token) {
            this.token = token;
        }
    }

    public static class UserResponse {
        public UUID id;
        public String username;
        public String email;
        public Integer followers;
        public Integer following;
        public Integer workouts;
        public Instant createdAt;
        public Instant updatedAt;
        public Instant deletedAt;

        public static UserResponse from(User user) {
            UserResponse response = new UserResponse();
            response.id = user.getId();
            response.username = user.getUsername();
            response.email = user.getEmail();
            response.followers = user.getFollowers();
            response.following = user.getFollowing();
            response.workouts = user.getWorkouts();
            response.createdAt = user.getCreatedAt();
            response.updatedAt = user.getUpdatedAt();
            response.deletedAt = user.getDeletedAt();
            return response;
        }
    }
}
