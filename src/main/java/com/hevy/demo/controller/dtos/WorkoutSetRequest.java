package com.hevy.demo.controller.dtos;

import java.math.BigDecimal;

import com.hevy.demo.models.enums.Series;

public record WorkoutSetRequest(String unit, Integer rep, Integer orderIndex, BigDecimal measure, Series type,
        Integer restTime) {

}
