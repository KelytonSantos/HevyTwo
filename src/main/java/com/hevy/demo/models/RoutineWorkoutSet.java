package com.hevy.demo.models;

import java.math.BigDecimal;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hevy.demo.models.enums.Series;
import com.hevy.demo.models.enums.SeriesConverter;

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

@Data
@Entity
@Table(name = "routine_workout_sets")
public class RoutineWorkoutSet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "routine_workout_id")
    private RoutineWorkout routineWorkout;

    @Convert(converter = SeriesConverter.class)
    @JdbcTypeCode(SqlTypes.OTHER)
    @Column(name = "set_type", columnDefinition = "series_type")
    private Series setType;

    @Column(name = "measure", precision = 5, scale = 2)
    private BigDecimal measure;

    @Column(name = "unit", length = 10)
    private String unit;

    @Column(name = "repetitions")
    private Integer repetitions;

    @Column(name = "order_index")
    private Integer orderIndex;

    @Column(name = "rest_time")
    private Integer restTime;
}
