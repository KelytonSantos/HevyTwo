package com.hevy.demo.models;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Convert;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.hevy.demo.models.enums.Series;
import com.hevy.demo.models.enums.SeriesConverter;
import com.hevy.demo.models.enums.StatusType;
import com.hevy.demo.models.enums.StatusTypeConverter;

@Data
@Entity
@Table(name = "workout_sets")
public class WorkoutSet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "workout_log_id")
    private WorkoutLog workoutLog;

    @Convert(converter = SeriesConverter.class)
    @JdbcTypeCode(SqlTypes.OTHER)
    @Column(name = "set_type", columnDefinition = "series_type")
    private Series setType;

    @Column(name = "unit", length = 10, nullable = false)
    private String unit;

    @Column(name = "measure", precision = 5, scale = 2)
    private BigDecimal measure;

    @Column(name = "repetitions")
    private Integer repetitions;

    @Column(name = "rest_time_actual")
    private Integer restTimeActual;

    @Column(name = "start_at")
    private Instant startAt;

    @Column(name = "end_at")
    private Instant endAt;

    @Convert(converter = StatusTypeConverter.class)
    @JdbcTypeCode(SqlTypes.OTHER)
    @Column(name = "status", columnDefinition = "status_type")
    private StatusType status;

    @Column(name = "order_index")
    private Integer orderIndex;
}
