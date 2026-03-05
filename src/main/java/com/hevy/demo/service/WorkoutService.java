package com.hevy.demo.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.hevy.demo.models.RoutineExecution;
import com.hevy.demo.models.RoutineWorkout;
import com.hevy.demo.models.WorkoutLog;
import com.hevy.demo.models.enums.StatusType;
import com.hevy.demo.repository.RoutineExecutionRepository;
import com.hevy.demo.repository.WorkoutLogRepository;

@Service
public class WorkoutService {

    @Autowired
    private RoutineExecutionRepository routineExecutionRepository;

    @Autowired
    private WorkoutLogRepository workoutLogRepository;

    @Autowired
    private RoutineWorkoutService routineWorkoutService;

    public List<WorkoutLog> createWorkoutLog(UUID routineExecutionId) {

        RoutineExecution routineExecution = routineExecutionRepository.findById(routineExecutionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Routine is empty or null"));

        List<RoutineWorkout> routineWorkouts = routineWorkoutService
                .getRoutineWorkout(routineExecution.getRoutine().getId());

        List<WorkoutLog> workoutLogs = routineWorkouts.stream().map(rw -> {
            WorkoutLog log = new WorkoutLog();
            log.setExecution(routineExecution); // Associa ao histórico atual
            log.setExerciseApiId(rw.getExerciseApiId());
            log.setWorkoutName(rw.getWorkoutName());
            log.setWorkoutImage(rw.getWorkoutImage());
            log.setDescription(rw.getDescription());
            return log;
        }).collect(Collectors.toList());

        return workoutLogRepository.saveAll(workoutLogs);
    }

    public List<WorkoutLog> getWorkoutLog(UUID routineExecutionId) {
        RoutineExecution routineExecution = routineExecutionRepository.findById(routineExecutionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Execução não encontrada"));

        if (!routineExecution.getStatus().equals(StatusType.PENDING)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "This workout session has already been finished or canceled.");
        }

        return workoutLogRepository.findAllByExecutionId(routineExecutionId);
    }

}

// ao startar uma rotina eu gero direto um workout log baseado em todos os
// exercicios daquela rotina em especifico
