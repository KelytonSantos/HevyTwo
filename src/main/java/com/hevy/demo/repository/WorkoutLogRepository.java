package com.hevy.demo.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hevy.demo.models.WorkoutLog;

public interface WorkoutLogRepository extends JpaRepository<WorkoutLog, UUID> {

    @Query("SELECT wl FROM WorkoutLog wl JOIN FETCH wl.execution WHERE wl.execution.id = :executionId")
    List<WorkoutLog> findAllByExecutionId(@Param("executionId") UUID executionId);

    @Query(value = """
            SELECT wl.* FROM workout_logs wl
            JOIN routines_executions re ON wl.execution_id = re.id
            WHERE wl.execution_id IN :executionIds
            AND wl.created_at >= DATE_TRUNC('month', NOW())
            AND wl.created_at < DATE_TRUNC('month', NOW()) + INTERVAL '1 month'
            """, nativeQuery = true)
    List<WorkoutLog> findAllByMonth(@Param("executionIds") List<UUID> executionIds);

    @Query(value = """
            SELECT wl.* FROM workout_logs wl
            JOIN routines_executions re ON wl.execution_id = re.id
            WHERE wl.execution_id IN (:executionIds)
            AND wl.created_at >= DATE_TRUNC('month', CURRENT_DATE - INTERVAL '1 month')
            AND wl.created_at < DATE_TRUNC('month', CURRENT_DATE);
                        """, nativeQuery = true)
    List<WorkoutLog> findAllByLastMonth(@Param("executionIds") List<UUID> executionIds);

}
