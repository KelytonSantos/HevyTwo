package com.hevy.demo.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(
            @Value("${cache.exercisedb.ttl-seconds:600}") long ttlSeconds,
            @Value("${cache.exercisedb.max-size:500}") int maxSize) {
        CaffeineCacheManager manager = new CaffeineCacheManager("exercisesByOffset", "exerciseById");
        manager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(ttlSeconds))
                .maximumSize(maxSize));
        return manager;
    }
}
