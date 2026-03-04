package com.hevy.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hevy.demo.client.ExerciseDBClient;
import com.hevy.demo.controller.dtos.Exercise;

@Service
public class ExerciseService {

    private static final int DEFAULT_LIMIT = 5;

    private final ExerciseDBClient exerciseDbClient;

    public ExerciseService(ExerciseDBClient exerciseDbClient) {
        this.exerciseDbClient = exerciseDbClient;
    }

    public List<Exercise> getExercisesByOffset(int offset) {
        return exerciseDbClient.fetchExercises(DEFAULT_LIMIT, offset);
    }

    public Exercise getExerciseById(String exerciseId) {
        return exerciseDbClient.fetchExercise(exerciseId);
    }

}
