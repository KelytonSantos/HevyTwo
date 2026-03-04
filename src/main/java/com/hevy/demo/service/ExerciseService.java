package com.hevy.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hevy.demo.client.ExerciseDBClient;
import com.hevy.demo.controller.dtos.Exercise;

@Service
public class ExerciseService {

    private final ExerciseDBClient exerciseDbClient;

    public ExerciseService(ExerciseDBClient exerciseDbClient) {
        this.exerciseDbClient = exerciseDbClient;
    }

    public List<Exercise> getExercisesByPage(int page, int size) {
        int offset = (page - 1) * size;
        return exerciseDbClient.fetchExercises(size, offset);
    }

    public Exercise getExerciseById(String exerciseId) {
        return exerciseDbClient.fetchExercise(exerciseId);
    }

}
