package com.hevy.demo.controller.dtos;

import java.math.BigDecimal;

public record RoutineWorkoutSetUpdateRequest(
        BigDecimal measure,
        Integer repetitions,
        Integer restTime) {
}
