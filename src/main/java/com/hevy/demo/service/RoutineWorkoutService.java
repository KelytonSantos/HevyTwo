package com.hevy.demo.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hevy.demo.controller.dtos.Exercise;
import com.hevy.demo.models.Routine;
import com.hevy.demo.models.RoutineWorkout;
import com.hevy.demo.repository.RoutineWorkoutRepository;

@Service
public class RoutineWorkoutService {

    @Autowired
    private RoutineWorkoutRepository routineWorkoutRepository;

    @Autowired
    private RoutineService routineService;

    public RoutineWorkout createRoutineWorkout(UUID routineId, Exercise exercise, String description) {
        RoutineWorkout routineWorkout = new RoutineWorkout();

        Routine routine = routineService.getRoutineById(routineId);

        routineWorkout.setExerciseApiId(exercise.exerciseId());
        routineWorkout.setRoutine(routine);
        routineWorkout.setWorkoutName(exercise.name());
        routineWorkout.setRestTimeSeconds(null);
        routineWorkout.setOrderIndex(null);
        routineWorkout.setWorkoutImage(exercise.gifUrl());
        routineWorkout.setDescription(description != null ? description : formatDescription(exercise));

        return routineWorkoutRepository.save(routineWorkout);
    }

    public RoutineWorkout createRoutineWorkout(UUID routineId, Exercise exercise) {
        return createRoutineWorkout(routineId, exercise, null);
    }

    private String formatDescription(Exercise exercise) {
        if (exercise == null || exercise.instructions() == null) {
            return null;
        }
        return String.join("\n", exercise.instructions());
    }

    public List<RoutineWorkout> getRoutineWorkout(UUID routineId) {
        return routineWorkoutRepository.findAllByRoutineIdWithJoin(routineId);
    }

}
