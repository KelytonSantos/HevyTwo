package com.hevy.demo.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hevy.demo.controller.dtos.DashboardResponse;
import com.hevy.demo.controller.dtos.GraphTimeOverMonthResponse;
import com.hevy.demo.controller.dtos.TimeAndDays;
import com.hevy.demo.models.RoutineExecution;
import com.hevy.demo.models.User;
import com.hevy.demo.models.WorkoutLog;
import com.hevy.demo.models.WorkoutSet;
import com.hevy.demo.repository.RoutineExecutionRepository;

/*

public record DashboardResponse(Integer workouts, Integer surplusBalance, Boolean isSurplus,
 Integer totalDuration,
        Integer volume, Integer totalSets, Integer topUsers) {

}

*/

@Service
public class DashboardService {

    @Autowired
    private WorkoutService workoutService;

    @Autowired
    private RoutineService routineService;

    @Autowired
    private RoutineExecutionRepository routineExecutionRepository;

    public DashboardResponse getMyDash(User user) {
        /*
         * (mes passado)
         * Instant start =
         * YearMonth.now().minusMonths(1).atDay(1).atStartOfDay().toInstant(ZoneOffset.
         * UTC);
         * Instant end =
         * YearMonth.now().atDay(1).atStartOfDay().toInstant(ZoneOffset.UTC);
         * 
         * -------------
         * (mes atual)
         * Instant start =
         * YearMonth.now().atDay(1).atStartOfDay().toInstant(ZoneOffset.UTC);
         * Instant end =
         * YearMonth.now().plusMonths(1).atDay(1).atStartOfDay().toInstant(ZoneOffset.
         * UTC);
         * 
         */

        Instant start = YearMonth.now().atDay(1).atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant end = YearMonth.now().plusMonths(1).atDay(1).atStartOfDay().toInstant(ZoneOffset.UTC);

        List<RoutineExecution> executions = routineService.getAllExecutionsByUserId(user.getId(), start, end);
        List<UUID> executionsId = executions.stream().map(r -> r.getId()).toList();

        List<WorkoutLog> logsMonth = workoutService.findAllByMonth(executionsId);
        List<WorkoutLog> loglastMonth = workoutService.findAllByLastMonth(executionsId);

        List<UUID> workoutLogs = logsMonth.stream().map(r -> r.getId()).toList();
        List<WorkoutSet> workoutSets = workoutService.findAllByListOfWorkoutLogId(workoutLogs);

        Integer duration = 0;

        for (RoutineExecution r : executions) {
            duration += r.getTotalTimeSeconds();
        }

        double avgHoursPerDay = (double) duration / 30 / 3600; // h/dia
        // ------- volume
        BigDecimal totalVolume = BigDecimal.ZERO;

        for (RoutineExecution r : executions) {

            totalVolume = totalVolume.add(r.getTotalWeightVolume());
        }

        // ------- workouts
        int workoutsMonth = logsMonth.size();
        int workoutsLastMonth = loglastMonth.size();

        int excess = workoutsLastMonth - workoutsMonth;
        boolean isSurplus = excess < 0; // true se ex < 0

        excess = Math.abs(excess);

        // ------ sets
        Integer sets = workoutSets.size();

        // ------ percentil de volume
        Integer volumePercentile = routineExecutionRepository
                .findVolumePercentile(user.getId(), start, end)
                .orElse(null);

        return new DashboardResponse(workoutsMonth, excess, isSurplus, duration,
                totalVolume.intValue(), sets, volumePercentile, avgHoursPerDay);
    }

    public GraphTimeOverMonthResponse graph(User user) {
        Instant start = YearMonth.now().atDay(1).atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant now = Instant.now();

        List<RoutineExecution> executions = routineService.getAllExecutionsByUserId(user.getId(), start, now);

        Map<Integer, Double> hoursByDay = new TreeMap<>();
        for (RoutineExecution exec : executions) {
            if (exec.getTotalTimeSeconds() == null)
                continue;
            int day = exec.getStartedAt().atZone(ZoneOffset.UTC).getDayOfMonth();
            double hours = exec.getTotalTimeSeconds() / 3600.0;
            hoursByDay.merge(day, hours, Double::sum);
        }

        int today = LocalDate.now(ZoneOffset.UTC).getDayOfMonth();
        List<TimeAndDays> data = new ArrayList<>();
        for (int d = 1; d <= today; d++) {
            data.add(new TimeAndDays(d, hoursByDay.getOrDefault(d, 0.0)));
        }

        GraphTimeOverMonthResponse response = new GraphTimeOverMonthResponse();
        response.setData(data);
        return response;
    }
}
