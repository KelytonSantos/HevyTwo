package com.hevy.demo.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hevy.demo.controller.dtos.Exercise;
import com.hevy.demo.service.ExerciseService;

import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/workouts")
public class WorkoutController {

    @Autowired
    private ExerciseService exerciseService;

    @GetMapping("/{exerciseId}")
    public ResponseEntity<Exercise> getExerciseById(@PathVariable @NotNull String exerciseId)
            throws IOException, InterruptedException {

        return ResponseEntity.ok().body(exerciseService.getExerciseById(exerciseId));
    }

    @GetMapping
    public ResponseEntity<List<Exercise>> getAllExercise(
            @RequestParam(name = "offset", defaultValue = "0") int offset) {

        return ResponseEntity.ok().body(exerciseService.getExercisesByOffset(offset));
    }

}
