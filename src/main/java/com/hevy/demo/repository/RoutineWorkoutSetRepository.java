package com.hevy.demo.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hevy.demo.models.RoutineWorkoutSet;

public interface RoutineWorkoutSetRepository extends JpaRepository<RoutineWorkoutSet, UUID> {

    List<RoutineWorkoutSet> findAllByRoutineWorkoutId(UUID routineWorkoutId);
}
