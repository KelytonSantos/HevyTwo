package com.hevy.demo.controller.dtos;

import java.util.UUID;

public record RoutineComposition(UUID routineId, String routineName, Integer totalWorkouts) {

}
