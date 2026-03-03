package com.hevy.demo.service;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hevy.demo.controller.dtos.RoutineRequest;
import com.hevy.demo.controller.dtos.RoutineResponse;
import com.hevy.demo.models.Routine;
import com.hevy.demo.models.User;
import com.hevy.demo.repository.RoutineRepository;

@Service
public class RoutineService {

    @Autowired
    private RoutineRepository routineRepository;

    public RoutineResponse geAll(User user) {
        List<Routine> list = routineRepository.findAllByUser(user);

        return new RoutineResponse(user.getId(), list);
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
