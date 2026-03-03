package com.hevy.demo.controller.dtos;

import java.util.List;
import java.util.UUID;

import com.hevy.demo.models.Routine;

public record RoutineResponse(UUID userId, List<Routine> routines) {

}
