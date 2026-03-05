package com.hevy.demo.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hevy.demo.models.WorkoutSet;
import com.hevy.demo.models.enums.StatusType;

public interface WorkoutSetRepository extends JpaRepository<WorkoutSet, UUID> {

    @Query("SELECT ws FROM WorkoutSet ws JOIN FETCH ws.workoutLog wl WHERE wl.id = :workoutLogId")
    List<WorkoutSet> findAllByWorkoutLogId(@Param("workoutLogId") UUID workoutLogId);

    @Query("SELECT ws FROM WorkoutSet ws JOIN FETCH ws.workoutLog wl WHERE wl.id = :workoutLogId AND ws.status = :status")
    List<WorkoutSet> findAllByWorkoutLogIdAndStatus(@Param("workoutLogId") UUID workoutLogId,
            @Param("status") StatusType status);

}
