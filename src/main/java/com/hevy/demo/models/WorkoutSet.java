package com.hevy.demo.models;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "workout_sets")
public class WorkoutSet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "workout_id")
    private Workout workout;

    @Enumerated(EnumType.STRING)
    @Column(name = "set")
    private Series setType;

    @Column(name = "unit", length = 10, nullable = false)
    private String unit;

    @Column(name = "measure", precision = 5, scale = 2)
    private BigDecimal measure;

    @Column(name = "repetitions")
    private Integer repetitions;
}

enum Series {
    DROP_SET,
    WARM_UP_SET,
    NORMAL_SET,
    FAILURE_SET
}
