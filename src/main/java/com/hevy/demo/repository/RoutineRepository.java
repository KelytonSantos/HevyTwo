package com.hevy.demo.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hevy.demo.models.Routine;
import com.hevy.demo.models.User;

public interface RoutineRepository extends JpaRepository<Routine, UUID> {

    @Query("SELECT r FROM Routine r WHERE r.deletedAt is NULL")
    List<Routine> findAllByUser(User user);

}
