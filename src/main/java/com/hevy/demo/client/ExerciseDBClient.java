package com.hevy.demo.client;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.hevy.demo.controller.dtos.Exercise;
import com.hevy.demo.controller.dtos.ExerciseApiResponse;

@Component
public class ExerciseDBClient {

    private static final Logger logger = LoggerFactory.getLogger(ExerciseDBClient.class);

    private final RestClient restClient;

    public ExerciseDBClient() {
        this.restClient = RestClient.builder()
                .baseUrl("https://exercisedb.dev/api/v1")
                .build();
    }

    public List<Exercise> fetchExercises(int limit, int offset) {
        try {
            ExerciseApiResponse<List<Exercise>> response = restClient.get()
                    .uri("/exercises/filter?limit={l}&offset={o}", limit, offset)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ExerciseApiResponse<List<Exercise>>>() {
                    });
            return response == null ? List.of() : response.data();
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                logger.warn("ExerciseDB rate limited (429) for exercises list. limit={}, offset={}", limit, offset);
                return List.of();
            }
            throw ex;
        }
        // https://exercisedb.dev/api/v1/exercises/{exerciseId}
    }

    public Exercise fetchExercise(String exerciseId) {
        try {
            ExerciseApiResponse<Exercise> response = restClient.get()
                    .uri("/exercises/{e}", exerciseId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ExerciseApiResponse<Exercise>>() {
                    });
            return response == null ? null : response.data();
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                logger.warn("ExerciseDB rate limited (429) for exercise id={}", exerciseId);
                return null;
            }
            throw ex;
        }
    }

}
