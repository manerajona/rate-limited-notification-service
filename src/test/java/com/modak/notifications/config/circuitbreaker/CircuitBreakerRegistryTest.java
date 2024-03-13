package com.modak.notifications.config.circuitbreaker;

import org.awaitility.Awaitility;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = CircuitBreakerProperties.class)
@TestPropertySource(properties = " com.modak.circuitbreaker.instances.default.duration-in-open-state=5s")
class CircuitBreakerRegistryTest {

    static final int TIMEOUT = 5;

    @Autowired
    CircuitBreakerProperties circuitBreakerProperties;
    CircuitBreakerRegistry registry;

    @BeforeEach
    public void SetUp() {
        assertThat(circuitBreakerProperties.getInstances()).isNotNull().isNotEmpty();
        this.registry = new CircuitBreakerRegistry(this.circuitBreakerProperties);
    }

    @Test
    void circuitBreakerOf_handle() {
        String notificationType = Instancio.create(String.class);
        String userId = Instancio.create(String.class);

        CompletableFuture<Void> successFuture = registry.circuitBreakerOf(notificationType, userId)
                .handle(() -> {});

        Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).until(successFuture::isDone);
        assertThat(successFuture).isNotNull().isCompleted();

        CompletableFuture<Void> exceptionallyFuture = registry.circuitBreakerOf(notificationType, userId)
                .handle(() -> {});

        Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).until(exceptionallyFuture::isDone);
        assertThat(exceptionallyFuture).isNotNull().isCompletedExceptionally();
    }
}