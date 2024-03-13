package com.modak.notifications.config.circuitbreaker;

import com.modak.notifications.common.TooManyNotificationsException;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CircuitBreakerRegistry {

    private static final String DEFAULT = "default";

    private final CircuitBreakerProperties properties;

    private final ConcurrentMap<KeyPair, OffsetDateTime> registry = new ConcurrentHashMap<>();

    public CircuitBreakerRegistry(CircuitBreakerProperties properties) {
        this.properties = properties;
    }

    public CircuitBreaker circuitBreakerOf(String notificationType, String userId) {
        KeyPair keyPair = KeyPair.of(userId, notificationType);
        CircuitBreakerProperties.RateLimiter rateLimiter =
                properties.getInstances().getOrDefault(notificationType, properties.getInstances().get(DEFAULT));
        return new CircuitBreaker(keyPair, registry, rateLimiter);
    }

    public static class CircuitBreaker {

        private final KeyPair keyPair;
        private final ConcurrentMap<KeyPair, OffsetDateTime> registry;
        private final CircuitBreakerProperties.RateLimiter rateLimiter;

        CircuitBreaker(KeyPair keyPair, ConcurrentMap<KeyPair, OffsetDateTime> registry,
                       CircuitBreakerProperties.RateLimiter rateLimiter) {
            this.registry = registry;
            this.keyPair = keyPair;
            this.rateLimiter = rateLimiter;
        }

        /**
         * Executes the {@link  Runnable} action if the call is made when the circuit is closed.
         *
         * @param runnable the action to run when the circuit is closed.
         * @return an empty {@link CompletableFuture} on success, or an exception if the call was made in open state.
         */
        public CompletableFuture<Void> handle(Runnable runnable) {
            return CompletableFuture.runAsync(() -> {
                OffsetDateTime currentCallDateTime = OffsetDateTime.now();
                OffsetDateTime lastCallDateTime = registry.get(keyPair);
                if (isInOpenState(currentCallDateTime, lastCallDateTime, rateLimiter.getDurationInOpenState())) {
                    throw new TooManyNotificationsException();
                }
                registry.put(keyPair, currentCallDateTime);
                runnable.run();
            });
        }

        private static boolean isInOpenState(OffsetDateTime currentCallDateTime, OffsetDateTime lastCallDateTime,
                                             Duration durationOpenState) {
            if (lastCallDateTime != null) {
                OffsetDateTime nextPossibleCallDateTime = lastCallDateTime.plus(durationOpenState);
                return currentCallDateTime.isBefore(nextPossibleCallDateTime);
            }
            return false;
        }
    }

    record KeyPair(String userId, String notificationType) {

        public static KeyPair of(String userId, String notificationType) {
            return new KeyPair(userId, notificationType);
        }
    }
}