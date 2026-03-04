package com.hevy.demo.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.hevy.demo.controller.dtos.RoutineRequest;
import com.hevy.demo.controller.dtos.RoutineResponse;
import com.hevy.demo.models.Routine;
import com.hevy.demo.models.User;
import com.hevy.demo.service.RoutineService;

@RestController
@RequestMapping("/routines")
public class RoutineController {

    @Autowired
    private RoutineService routineService;

    @GetMapping("/test")
    public String hello() {
        return "alo porteiro";
    }

    @GetMapping
    public ResponseEntity<RoutineResponse> getAllMyRoutines(Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok().body(routineService.geAll(user));
    }

    @PostMapping
    public ResponseEntity<Routine> createRoutine(@RequestBody RoutineRequest routineRequest,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        Routine response = routineService.create(routineRequest, user);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();
        return ResponseEntity.created(uri).body(response);
    }

}
