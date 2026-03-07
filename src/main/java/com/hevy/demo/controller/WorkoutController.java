package com.hevy.demo.controller;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.hevy.demo.controller.dtos.Exercise;
import com.hevy.demo.controller.dtos.RoutineWorkoutSetRequest;
import com.hevy.demo.controller.dtos.RoutineWorkoutSetUpdateRequest;
import com.hevy.demo.controller.dtos.WorkoutSetRequest;
import com.hevy.demo.models.RoutineWorkout;
import com.hevy.demo.models.RoutineWorkoutSet;
import com.hevy.demo.models.WorkoutLog;
import com.hevy.demo.models.WorkoutSet;
import com.hevy.demo.service.ExerciseService;
import com.hevy.demo.service.RoutineWorkoutService;
import com.hevy.demo.service.WorkoutService;

import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/workouts")
public class WorkoutController {

    @Autowired
    private ExerciseService exerciseService;

    @Autowired
    private RoutineWorkoutService routineWorkoutService;

    @Autowired
    private WorkoutService workoutService;

    @GetMapping("/api/exercise/bd/{exerciseId}")
    public ResponseEntity<Exercise> getExerciseById(@PathVariable @NotNull String exerciseId,
            Authentication authentication) throws IOException, InterruptedException {

        return ResponseEntity.ok().body(exerciseService.getExerciseById(exerciseId, authentication));
    }

    @GetMapping("/api/exercise/bd")
    public ResponseEntity<List<Exercise>> getAllExercise(
            @RequestParam(name = "offset", defaultValue = "0") int offset,
            Authentication authentication) {

        return ResponseEntity.ok().body(exerciseService.getExercisesByOffset(offset, authentication));
    }

    @PostMapping("/{exerciseId}/{routineId}")
    public ResponseEntity<RoutineWorkout> createWorkout(@PathVariable String exerciseId, @PathVariable UUID routineId,
            Authentication authentication) {

        Exercise exercise = exerciseService.getExerciseById(exerciseId, authentication);
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

    @GetMapping("/my/routine/{routineId}") // todos os exercicios de uma rotina
    public ResponseEntity<List<RoutineWorkout>> getRoutineWorkout(@PathVariable UUID routineId) {

        return ResponseEntity.ok().body(routineWorkoutService.getRoutineWorkout(routineId));
    }

    @GetMapping("/routine/workout/{routineWorkoutId}")
    public ResponseEntity<RoutineWorkout> getRoutineWorkoutById(@PathVariable UUID routineWorkoutId) {
        return ResponseEntity.ok(routineWorkoutService.getRoutineWorkoutById(routineWorkoutId));
    }

    @DeleteMapping("/routine/workout/{routineWorkoutId}")
    public ResponseEntity<Void> deleteRoutineWorkout(@PathVariable UUID routineWorkoutId) {
        routineWorkoutService.deleteRoutineWorkout(routineWorkoutId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my/{routineExecutionId}") // todos os exercicios daquela routine execution (em aberto, so pode haver
                                            // um em aberto)
    public ResponseEntity<List<WorkoutLog>> getMyWorkout(@PathVariable UUID routineExecutionId) {
        List<WorkoutLog> logs = workoutService.getWorkoutLog(routineExecutionId);

        return ResponseEntity.ok(logs);
    }

    @PostMapping("/init/{workoutLogId}")
    public ResponseEntity<WorkoutSet> createWorkoutSet(@PathVariable UUID workoutLogId,
            @RequestBody WorkoutSetRequest workoutSetRequest) {

        WorkoutSet workoutSet = workoutService.createWorkoutSet(workoutLogId, workoutSetRequest);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(workoutSet.getId())
                .toUri();

        return ResponseEntity.created(uri).body(workoutSet);
    }

    @PostMapping("/set/{workoutSetId}/finish")
    public ResponseEntity<WorkoutSet> finishWorkoutSet(@PathVariable UUID workoutSetId) {
        WorkoutSet workoutSet = workoutService.finishWorkoutSet(workoutSetId);

        return ResponseEntity.ok(workoutSet);
    }

    @PostMapping("/set/{workoutSetId}/cancel")
    public ResponseEntity<WorkoutSet> cancelWorkoutSet(@PathVariable UUID workoutSetId) {
        WorkoutSet workoutSet = workoutService.cancelWorkoutSet(workoutSetId);

        return ResponseEntity.ok(workoutSet);
    }

    @GetMapping("/set/log/{workoutLogId}/pending")
    public ResponseEntity<List<WorkoutSet>> getPendingWorkoutSets(@PathVariable UUID workoutLogId) {
        return ResponseEntity.ok(workoutService.getPendingWorkoutSets(workoutLogId));
    }

    @GetMapping("/set/log/{workoutLogId}")
    public ResponseEntity<List<WorkoutSet>> getAllWorkoutSets(@PathVariable UUID workoutLogId) {
        return ResponseEntity.ok(workoutService.getAllWorkoutSets(workoutLogId));
    }

    // ---- routine workout sets (template) ----

    @PostMapping("/routine/workout/{routineWorkoutId}/set")
    public ResponseEntity<RoutineWorkoutSet> createRoutineWorkoutSet(
            @PathVariable UUID routineWorkoutId,
            @RequestBody RoutineWorkoutSetRequest request) {

        RoutineWorkoutSet set = routineWorkoutService.createRoutineWorkoutSet(routineWorkoutId, request);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(set.getId())
                .toUri();
        return ResponseEntity.created(uri).body(set);
    }

    @GetMapping("/routine/workout/{routineWorkoutId}/set")
    public ResponseEntity<List<RoutineWorkoutSet>> getRoutineWorkoutSets(@PathVariable UUID routineWorkoutId) {
        return ResponseEntity.ok(routineWorkoutService.getRoutineWorkoutSets(routineWorkoutId));
    }

    @PatchMapping("/routine/workout/set/{routineWorkoutSetId}")
    public ResponseEntity<RoutineWorkoutSet> updateRoutineWorkoutSet(
            @PathVariable UUID routineWorkoutSetId,
            @RequestBody RoutineWorkoutSetUpdateRequest request) {
        return ResponseEntity.ok(routineWorkoutService.updateRoutineWorkoutSet(routineWorkoutSetId, request));
    }

    @DeleteMapping("/routine/workout/set/{routineWorkoutSetId}")
    public ResponseEntity<Void> deleteRoutineWorkoutSet(@PathVariable UUID routineWorkoutSetId) {
        routineWorkoutService.deleteRoutineWorkoutSet(routineWorkoutSetId);
        return ResponseEntity.noContent().build();
    }
}