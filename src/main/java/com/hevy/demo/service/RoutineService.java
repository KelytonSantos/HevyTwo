package com.hevy.demo.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.hevy.demo.controller.dtos.RoutineComposition;
import com.hevy.demo.controller.dtos.RoutineRequest;
import com.hevy.demo.controller.dtos.RoutineResponse;
import com.hevy.demo.models.Routine;
import com.hevy.demo.models.RoutineExecution;
import com.hevy.demo.models.User;
import com.hevy.demo.models.enums.StatusType;
import com.hevy.demo.repository.RoutineExecutionRepository;
import com.hevy.demo.repository.RoutineRepository;
import com.hevy.demo.service.exceptions.ResourceNotFoundException;

@Service
public class RoutineService {

    @Autowired
    private RoutineRepository routineRepository;

    @Autowired
    private RoutineExecutionRepository routineExecutionRepository;

    @Autowired
    private WorkoutService workoutService;

    // public record RoutineResponse(UUID userId, Integer totalRoutines,
    // List<RoutineComposition> routines) {

    public RoutineResponse getAll(User user) {
        List<Routine> list = routineRepository.findAllByUser(user);
        int totalRoutines = list.size();

        List<RoutineComposition> routineCompositionList = new ArrayList<>();

        for (Routine r : list) {
            int totalWorkoutsInRoutine = r.getWorkouts().size();
            RoutineComposition rc = new RoutineComposition(r.getId(), r.getRoutineName(), totalWorkoutsInRoutine);
            routineCompositionList.add(rc);
        }

        return new RoutineResponse(user.getId(), totalRoutines, routineCompositionList);
    }

    public Routine getRoutineById(UUID routineId) {
        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new ResourceNotFoundException("Routine not found"));

        return routine;
    }

    public Routine create(RoutineRequest routineRequest, User user) {
        Routine routine = new Routine();
        routine.setCreatedAt(Instant.now());
        routine.setRoutineName(routineRequest.routineName());
        routine.setUser(user);

        Routine savedRoutine = routineRepository.save(routine);

        return savedRoutine;

    }

    public RoutineExecution createRoutineExecution(User user, UUID routineId) {
        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new ResourceNotFoundException("Routine not found"));

        if (routine.getWorkouts().isEmpty() || routine.getWorkouts() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Routine is empty or null");
        }

        RoutineExecution routineExecution = new RoutineExecution();

        routineExecution.setRoutine(routine);
        routineExecution.setUser(user);
        routineExecution.setStartedAt(Instant.now());
        routineExecution.setStatus(StatusType.PENDING);
        routineExecution.setTotalWeightVolume(BigDecimal.valueOf(0, 0));

        RoutineExecution routineExecutionSaved = routineExecutionRepository.save(routineExecution);
        workoutService.createWorkoutLog(routineExecutionSaved.getId());

        return routineExecutionSaved;
    }

    public RoutineExecution finishRoutineExecution(UUID routineExecutionId) {
        RoutineExecution routineExecution = routineExecutionRepository.findById(routineExecutionId).orElseThrow(
                () -> new ResourceNotFoundException(
                        "Routine Execution id" + routineExecutionId.toString() + " not found"));

        routineExecution.setEndedAt(Instant.now());

        long seconds = Duration.between(routineExecution.getStartedAt(), routineExecution.getEndedAt()).getSeconds();
        routineExecution.setTotalTimeSeconds((int) seconds);

        routineExecution.setStatus(StatusType.COMPLETED);

        return routineExecutionRepository.save(routineExecution);
    }

    public void delete(UUID routineId) {
        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new ResourceNotFoundException("Routine not found"));

        routine.setDeletedAt(Instant.now());

        routineRepository.save(routine);

    }

}

// o peso total do routine execution é atualizado apartir de cada finalização de
// workoutset

// validar no front se tem ou n exercicio feito
// *added implementado */
