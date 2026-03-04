package com.hevy.demo.controller.dtos;

import java.time.Instant;
import java.util.UUID;

public record WorkoutResponse(UUID routineId, String exerciseId, Instant createdAt) {

}
