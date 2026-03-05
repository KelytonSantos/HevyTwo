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
@Table(name = "routine_workouts")
public class RoutineWorkout {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "routine_id")
    private Routine routine;

    @Column(name = "exercise_api_id")
    private String exerciseApiId;

    @Column(name = "workout_name")
    private String workoutName;

    @Column(name = "workout_image")
    private String workoutImage;

    @Column(name = "description")
    private String description;

    @Column(name = "rest_time_seconds")
    private Integer restTimeSeconds;

    @Column(name = "order_index")
    private Integer orderIndex;
}
