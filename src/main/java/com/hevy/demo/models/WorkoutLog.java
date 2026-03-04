package com.hevy.demo.models;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "workout_logs")
public class WorkoutLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "execution_id")
    private RoutineExecution execution;

    @Column(name = "exercise_api_id")
    private Integer exerciseApiId;

    @Column(name = "workout_name")
    private String workoutName;

    @Column(name = "workout_image", columnDefinition = "bytea")
    private byte[] workoutImage;
}
