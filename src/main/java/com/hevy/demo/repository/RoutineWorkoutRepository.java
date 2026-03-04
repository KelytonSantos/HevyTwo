package com.hevy.demo.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hevy.demo.models.RoutineWorkout;

public interface RoutineWorkoutRepository extends JpaRepository<RoutineWorkout, UUID> {

    @Query("SELECT rw FROM RoutineWorkout rw JOIN FETCH rw.routine WHERE rw.routine.id = :routineId")
    List<RoutineWorkout> findAllByRoutineIdWithJoin(UUID routineId);
}
