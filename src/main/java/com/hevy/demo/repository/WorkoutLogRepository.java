package com.hevy.demo.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hevy.demo.models.WorkoutLog;

public interface WorkoutLogRepository extends JpaRepository<WorkoutLog, UUID> {

}
