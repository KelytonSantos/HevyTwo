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

    public RoutineWorkout createRoutineWorkout(UUID routineId, Exercise exercise) {
        RoutineWorkout routineWorkout = new RoutineWorkout();

        Routine routine = routineService.getRoutineById(routineId);

        routineWorkout.setExerciseApiId(exercise.exerciseId());
        routineWorkout.setRoutine(routine);
        routineWorkout.setWorkoutName(exercise.name());
        routineWorkout.setRestTimeSeconds(null);
        routineWorkout.setOrderIndex(null);
        routineWorkout.setWorkoutImage(exercise.gifUrl());

        return routineWorkoutRepository.save(routineWorkout);
    }

    public List<RoutineWorkout> getRoutineWorkout(UUID routineId) {
        return routineWorkoutRepository.findAllByRoutineIdWithJoin(routineId);
    }

}
