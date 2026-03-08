package com.hevy.demo.controller.dtos;

public record DashboardResponse(Integer workouts, Integer surplusBalance, Boolean isSurplus, Integer totalDuration,
        Integer volume, Integer totalSets, Integer topUsers, Double avgHours) {

}
