package com.hevy.demo.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hevy.demo.controller.dtos.AuthResponse;
import com.hevy.demo.controller.dtos.LoginRequest;
import com.hevy.demo.controller.dtos.RegisterRequest;
import com.hevy.demo.models.User;
import com.hevy.demo.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class AuthService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        User user = new User();
        user.setCreatedAt(Instant.now());
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setFollowers(0);
        user.setFollowing(0);
        user.setWorkouts(0);
        user.setPassword(passwordEncoder.encode(request.password()));

        User userSaved = userRepository.save(user);

        return new AuthResponse(userSaved.getId(), userSaved.getUsername(), userSaved.getFollowers(),
                userSaved.getFollowing(), userSaved.getWorkouts(), userSaved.getProfileImage(),
                userSaved.getCreatedAt(), jwtService.generateToken(userSaved));

    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid user or password");
        }

        return new AuthResponse(user.getId(), user.getUsername(), user.getFollowers(),
                user.getFollowing(), user.getWorkouts(), user.getProfileImage(),
                user.getCreatedAt(), jwtService.generateToken(user));

    }

}
