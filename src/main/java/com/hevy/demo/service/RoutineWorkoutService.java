package com.hevy.demo.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.hevy.demo.controller.dtos.Exercise;
import com.hevy.demo.controller.dtos.RoutineWorkoutSetRequest;
import com.hevy.demo.controller.dtos.RoutineWorkoutSetUpdateRequest;
import com.hevy.demo.models.Routine;
import com.hevy.demo.models.RoutineWorkout;
import com.hevy.demo.models.RoutineWorkoutSet;
import com.hevy.demo.repository.RoutineWorkoutRepository;
import com.hevy.demo.repository.RoutineWorkoutSetRepository;
import com.hevy.demo.repository.RoutineRepository;
import com.hevy.demo.service.exceptions.ResourceNotFoundException;

@Service
public class RoutineWorkoutService {

    @Autowired
    private RoutineWorkoutRepository routineWorkoutRepository;

    @Autowired
    private RoutineRepository routineRepository;

    @Autowired
    private RoutineWorkoutSetRepository routineWorkoutSetRepository;

    public RoutineWorkout createRoutineWorkout(UUID routineId, Exercise exercise, String description) {
        RoutineWorkout routineWorkout = new RoutineWorkout();

        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new ResourceNotFoundException("Routine not found"));

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

    public RoutineWorkout getRoutineWorkoutById(UUID routineWorkoutId) {
        return routineWorkoutRepository.findById(routineWorkoutId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "RoutineWorkout not found"));
    }

    public void deleteRoutineWorkout(UUID routineWorkoutId) {
        if (!routineWorkoutRepository.existsById(routineWorkoutId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "RoutineWorkout not found");
        }
        routineWorkoutRepository.deleteById(routineWorkoutId);
    }

    public RoutineWorkoutSet createRoutineWorkoutSet(UUID routineWorkoutId, RoutineWorkoutSetRequest request) {
        RoutineWorkout routineWorkout = routineWorkoutRepository.findById(routineWorkoutId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "RoutineWorkout not found"));

        RoutineWorkoutSet set = new RoutineWorkoutSet();
        set.setRoutineWorkout(routineWorkout);
        set.setSetType(request.setType());
        set.setMeasure(request.measure());
        set.setUnit(request.unit());
        set.setRepetitions(request.repetitions());
        set.setOrderIndex(request.orderIndex());
        set.setRestTime(request.restTime());

        return routineWorkoutSetRepository.save(set);
    }

    public List<RoutineWorkoutSet> getRoutineWorkoutSets(UUID routineWorkoutId) {
        if (!routineWorkoutRepository.existsById(routineWorkoutId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "RoutineWorkout not found");
        }
        return routineWorkoutSetRepository.findAllByRoutineWorkoutId(routineWorkoutId);
    }

    public RoutineWorkoutSet updateRoutineWorkoutSet(UUID routineWorkoutSetId, RoutineWorkoutSetUpdateRequest request) {
        RoutineWorkoutSet set = routineWorkoutSetRepository.findById(routineWorkoutSetId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "RoutineWorkoutSet not found"));

        if (request.measure() != null)
            set.setMeasure(request.measure());
        if (request.repetitions() != null)
            set.setRepetitions(request.repetitions());
        if (request.restTime() != null)
            set.setRestTime(request.restTime());

        return routineWorkoutSetRepository.save(set);
    }

    public void deleteRoutineWorkoutSet(UUID routineWorkoutSetId) {
        if (!routineWorkoutSetRepository.existsById(routineWorkoutSetId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "RoutineWorkoutSet not found");
        }
        routineWorkoutSetRepository.deleteById(routineWorkoutSetId);
    }

}
