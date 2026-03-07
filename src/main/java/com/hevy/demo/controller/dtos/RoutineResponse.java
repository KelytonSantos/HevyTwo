package com.hevy.demo.controller.dtos;

import java.util.List;
import java.util.UUID;

public record RoutineResponse(UUID userId, Integer totalRoutines, List<RoutineComposition> routines) {

}
