package com.hevy.demo.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hevy.demo.models.WorkoutSet;

public interface WorkoutSetRepository extends JpaRepository<WorkoutSet, UUID> {

}
