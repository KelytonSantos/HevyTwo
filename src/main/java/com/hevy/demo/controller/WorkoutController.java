package com.hevy.demo.controller;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.hevy.demo.controller.dtos.Exercise;
import com.hevy.demo.models.RoutineWorkout;
import com.hevy.demo.service.ExerciseService;
import com.hevy.demo.service.RoutineWorkoutService;

import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/workouts")
public class WorkoutController {

    @Autowired
    private ExerciseService exerciseService;

    @Autowired
    private RoutineWorkoutService routineWorkoutService;

    @GetMapping("/api/exercise/bd/{exerciseId}")
    public ResponseEntity<Exercise> getExerciseById(@PathVariable @NotNull String exerciseId)
            throws IOException, InterruptedException {

        return ResponseEntity.ok().body(exerciseService.getExerciseById(exerciseId));
    }

    @GetMapping
    public ResponseEntity<List<Exercise>> getAllExercise(
            @RequestParam(name = "offset", defaultValue = "0") int offset) {

        return ResponseEntity.ok().body(exerciseService.getExercisesByOffset(offset));
    }

    @PostMapping("/{exerciseId}/{routineId}")
    public ResponseEntity<RoutineWorkout> createWorkout(@PathVariable String exerciseId, @PathVariable UUID routineId,
            Authentication authentication) {

        Exercise exercise = exerciseService.getExerciseById(exerciseId);
        String description = formatDescription(exercise);
        RoutineWorkout routineWorkout = routineWorkoutService.createRoutineWorkout(routineId, exercise, description);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(routineWorkout.getId())
                .toUri();
        return ResponseEntity.created(uri).body(routineWorkout);
    }

    private String formatDescription(Exercise exercise) {
        if (exercise == null || exercise.instructions() == null) {
            return null;
        }
        return String.join("\n", exercise.instructions());
    }

    @GetMapping("/my/routine/{routineWorkoutId}")
    public ResponseEntity<List<RoutineWorkout>> getRoutineWorkout(@PathVariable UUID routineWorkoutId) {

        return ResponseEntity.ok().body(routineWorkoutService.getRoutineWorkout(routineWorkoutId));
    }

}