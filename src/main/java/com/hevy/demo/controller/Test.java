package com.hevy.demo.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hevy.demo.controller.dtos.Exercise;
import com.hevy.demo.service.ExerciseService;

import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/test")
public class Test {

    @Autowired
    private ExerciseService testService;

    @GetMapping("{exerciseId}")
    public ResponseEntity<Exercise> test(@PathVariable @NotNull String exerciseId)
            throws IOException, InterruptedException {

        return ResponseEntity.ok().body(testService.getExerciseById(exerciseId));
    }

}
