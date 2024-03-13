package com.modak.notifications.config;

import com.modak.notifications.config.circuitbreaker.CircuitBreakerProperties;
import com.modak.notifications.config.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class CircuitBreakerConfiguration {

    @Bean
    CircuitBreakerRegistry registry(CircuitBreakerProperties properties) {
        return new CircuitBreakerRegistry(properties);
    }
}