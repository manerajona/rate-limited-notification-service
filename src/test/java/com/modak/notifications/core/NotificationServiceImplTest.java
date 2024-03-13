package com.modak.notifications.core;

import com.modak.notifications.common.TooManyNotificationsException;
import com.modak.notifications.config.circuitbreaker.CircuitBreakerRegistry;
import com.modak.notifications.ports.output.EmailGateway;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @InjectMocks
    NotificationServiceImpl service;
    @Mock
    EmailGateway emailGateway;
    @Mock
    CircuitBreakerRegistry circuitBreakerRegistry;
    @Mock
    CircuitBreakerRegistry.CircuitBreaker circuitBreaker;

    @Test
    void send_success() {
        CompletableFuture<Void> givenFuture = CompletableFuture.runAsync(() -> {});
        given(circuitBreakerRegistry.circuitBreakerOf(anyString(), anyString())).willReturn(circuitBreaker);
        given(circuitBreaker.handle(any())).willReturn(givenFuture);
        service.send(Instancio.create(String.class), Instancio.create(String.class), Instancio.create(String.class));

        assertThat(givenFuture).isCompleted();
    }

    @Test
    void send_fail() {
        CompletableFuture<Void> givenFuture = CompletableFuture.failedFuture(new TooManyNotificationsException());
        given(circuitBreakerRegistry.circuitBreakerOf(anyString(), anyString())).willReturn(circuitBreaker);
        given(circuitBreaker.handle(any())).willReturn(givenFuture);
        service.send(Instancio.create(String.class), Instancio.create(String.class), Instancio.create(String.class));

        assertThat(givenFuture).isCompletedExceptionally();
    }
}