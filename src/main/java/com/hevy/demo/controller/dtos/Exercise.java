package com.hevy.demo.controller.dtos;

import java.util.List;

public record Exercise(String exerciseId, String name, String gifUrl,
        List<String> targetMuscles, List<String> instructions) {

}
