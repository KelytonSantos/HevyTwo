package com.hevy.demo.controller.dtos;

import java.math.BigDecimal;

import com.hevy.demo.models.enums.Series;

public record RoutineWorkoutSetRequest(
        Series setType,
        BigDecimal measure,
        String unit,
        Integer repetitions,
        Integer orderIndex,
        Integer restTime) {
}
