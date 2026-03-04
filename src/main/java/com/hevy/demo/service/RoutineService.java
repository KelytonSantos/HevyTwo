package com.hevy.demo.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hevy.demo.controller.dtos.RoutineRequest;
import com.hevy.demo.controller.dtos.RoutineResponse;
import com.hevy.demo.models.Routine;
import com.hevy.demo.models.User;
import com.hevy.demo.repository.RoutineRepository;
import com.hevy.demo.service.exceptions.ResourceNotFoundException;

@Service
public class RoutineService {

    @Autowired
    private RoutineRepository routineRepository;

    public RoutineResponse getAll(User user) {
        List<Routine> list = routineRepository.findAllByUser(user);

        return new RoutineResponse(user.getId(), list);
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
}
