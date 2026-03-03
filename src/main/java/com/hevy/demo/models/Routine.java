package com.hevy.demo.models;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "routines")
public class Routine {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "routine_name", nullable = false)
    private String routineName;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = true)
    private Instant updatedAt;

    @Column(name = "deleted_at", nullable = true)
    private Instant deletedAt;

    @JsonIgnore
    @OneToMany(mappedBy = "routine")
    private List<Workout> workouts = new ArrayList<>();
}
