package com.hevy.demo.models;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.hevy.demo.models.enums.StatusType;
import com.hevy.demo.models.enums.StatusTypeConverter;

@Data
@Entity
@Table(name = "routines_executions")
public class RoutineExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "routine_id")
    private Routine routine;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Convert(converter = StatusTypeConverter.class)
    @JdbcTypeCode(SqlTypes.OTHER)
    @Column(name = "status", columnDefinition = "status_type")
    private StatusType status;

    @Column(name = "total_time_seconds")
    private Integer totalTimeSeconds;

    @Column(name = "total_weight_volume")
    private BigDecimal totalWeightVolume;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "ended_at")
    private Instant endedAt;
}
