package com.hevy.demo.controller.dtos;

import java.time.Instant;
import java.util.UUID;

public record AuthResponse(UUID id, String username, Integer followers, Integer following, Integer workouts,
                byte[] profileImg,
                Instant createdAt, String jwt) {

}
