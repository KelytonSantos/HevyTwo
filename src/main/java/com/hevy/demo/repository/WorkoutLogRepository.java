package com.hevy.demo.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hevy.demo.models.WorkoutLog;

public interface WorkoutLogRepository extends JpaRepository<WorkoutLog, UUID> {

    @Query("SELECT wl FROM WorkoutLog wl JOIN FETCH wl.execution WHERE wl.execution.id = :executionId")
    List<WorkoutLog> findAllByExecutionId(@Param("executionId") UUID executionId);

}
