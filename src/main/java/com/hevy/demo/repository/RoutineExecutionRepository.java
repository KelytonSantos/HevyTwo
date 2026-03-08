package com.hevy.demo.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hevy.demo.models.RoutineExecution;

public interface RoutineExecutionRepository extends JpaRepository<RoutineExecution, UUID> {

    @Query(value = """
            SELECT re.* FROM routines_executions re
            JOIN users u ON re.user_id = u.id
            WHERE re.user_id = :userId
            AND re.status IN ('canceled', 'completed')
            AND re.started_at >= :start
            AND re.started_at < :end
            """, nativeQuery = true)
    List<RoutineExecution> findAllRoutineExecutionByUserId(@Param("userId") UUID userId, @Param("start") Instant start,
            @Param("end") Instant end);

    @Query(value = """
            SELECT CAST(ROUND(CAST((1 - PERCENT_RANK() OVER (ORDER BY total_volume)) * 100 AS numeric)) AS integer)
            FROM (
                SELECT user_id, SUM(total_weight_volume) AS total_volume
                FROM routines_executions
                WHERE started_at >= :start
                AND started_at < :end
                AND status IN ('canceled', 'completed')
                GROUP BY user_id
            ) volumes
            WHERE user_id = :userId
            """, nativeQuery = true)
    Optional<Integer> findVolumePercentile(@Param("userId") UUID userId, @Param("start") Instant start,
            @Param("end") Instant end);
}
