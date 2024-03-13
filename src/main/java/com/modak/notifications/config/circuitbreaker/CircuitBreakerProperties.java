package com.modak.notifications.config.circuitbreaker;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties("com.modak.circuitbreaker")
public class CircuitBreakerProperties {

    private Map<String, RateLimiter> instances = new HashMap<>();

    public Map<String, RateLimiter> getInstances() {
        return Map.copyOf(instances);
    }

    public void setInstances(Map<String, RateLimiter> instances) {
        this.instances = instances;
    }

    public static class RateLimiter {
        private Duration durationInOpenState;

        public Duration getDurationInOpenState() {
            return durationInOpenState;
        }

        public void setDurationInOpenState(Duration durationInOpenState) {
            this.durationInOpenState = durationInOpenState;
        }
    }
}