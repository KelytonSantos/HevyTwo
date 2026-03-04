package com.hevy.demo.client;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.hevy.demo.controller.dtos.Exercise;
import com.hevy.demo.controller.dtos.ExerciseApiResponse;

@Component
public class ExerciseDBClient {

    private final RestClient restClient;

    public ExerciseDBClient() {
        this.restClient = RestClient.builder()
                .baseUrl("https://exercisedb.dev/api/v1")
                .build();
    }

    public List<Exercise> fetchExercises(int limit, int offset) {
        ExerciseApiResponse<List<Exercise>> response = restClient.get()
                .uri("/exercises/filter?limit={l}&offset={o}", limit, offset)
                .retrieve()
                .body(new ParameterizedTypeReference<ExerciseApiResponse<List<Exercise>>>() {
                });
        return response == null ? List.of() : response.data();
        // https://exercisedb.dev/api/v1/exercises/{exerciseId}
    }

    public Exercise fetchExercise(String exerciseId) {
        ExerciseApiResponse<Exercise> response = restClient.get()
                .uri("/exercises/{e}", exerciseId)
                .retrieve()
                .body(new ParameterizedTypeReference<ExerciseApiResponse<Exercise>>() {
                });
        return response == null ? null : response.data();
    }

}
