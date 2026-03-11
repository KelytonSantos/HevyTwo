package com.hevy.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hevy.demo.controller.dtos.DashboardResponse;
import com.hevy.demo.controller.dtos.GraphTimeOverMonthResponse;
import com.hevy.demo.models.User;
import com.hevy.demo.service.DashboardService;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardResponse> getMyDash(Authentication auth) {
        User user = (User) auth.getPrincipal();

        return ResponseEntity.ok().body(dashboardService.getMyDash(user));
    }

    // hora = y,
    // dias = x

    @GetMapping("/graph")
    public ResponseEntity<GraphTimeOverMonthResponse> getGraphTimeOverMonth(Authentication auth) {
        User user = (User) auth.getPrincipal();
        return ResponseEntity.ok().body(dashboardService.graph(user));
    }

}
