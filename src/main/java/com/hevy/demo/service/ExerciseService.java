package com.hevy.demo.service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.hevy.demo.client.ExerciseDBClient;
import com.hevy.demo.controller.dtos.Exercise;

@Service
public class ExerciseService {

    private static final int DEFAULT_LIMIT = 2;
    private static final long MIN_INTERVAL_MILLIS = 2000L;

    private final ExerciseDBClient exerciseDbClient;
    private final ConcurrentMap<String, AtomicLong> lastCallByUser = new ConcurrentHashMap<>();

    public ExerciseService(ExerciseDBClient exerciseDbClient) {
        this.exerciseDbClient = exerciseDbClient;
    }

    @Cacheable(cacheNames = "exercisesByOffset", key = "#offset")
    public List<Exercise> getExercisesByOffset(int offset, Authentication authentication) {
        throttle(authentication);
        return exerciseDbClient.fetchExercises(DEFAULT_LIMIT, offset);
    }

    @Cacheable(cacheNames = "exerciseById", key = "#exerciseId", unless = "#result == null")
    public Exercise getExerciseById(String exerciseId, Authentication authentication) {
        throttle(authentication);
        return exerciseDbClient.fetchExercise(exerciseId);
    }

    private void throttle(Authentication authentication) {
        String userKey = authentication == null ? "unknown" : authentication.getName();
        AtomicLong lastCall = lastCallByUser.computeIfAbsent(userKey, key -> new AtomicLong(0L));

        while (true) {
            long previous = lastCall.get();
            long now = System.currentTimeMillis();
            long waitMillis = MIN_INTERVAL_MILLIS - (now - previous);

            if (waitMillis <= 0L) {
                if (lastCall.compareAndSet(previous, now)) {
                    return;
                }
                continue;
            }

            try {
                Thread.sleep(waitMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Throttle interrupted", e);
            }
        }
    }

}
