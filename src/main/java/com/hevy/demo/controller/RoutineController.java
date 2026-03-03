package com.hevy.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/routines")
public class RoutineController {

    @GetMapping("/test")
    public String hello() {
        return "alo porteiro";
    }

}
