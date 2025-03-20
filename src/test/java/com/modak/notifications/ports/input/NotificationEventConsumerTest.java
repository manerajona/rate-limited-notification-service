package com.modak.notifications.ports.input;

import com.modak.notifications.common.TooManyNotificationsException;
import com.modak.notifications.core.NotificationService;
import org.instancio.Instancio;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class NotificationEventConsumerTest {

    @Autowired
    NotificationEventConsumer consumer;

    @MockBean
    NotificationService service;

    @Test
    void testStatusNotification_RateLimit() {
        String userId = UUID.randomUUID().toString();
        NotificationEvent event = new NotificationEvent("Status", Instancio.of(String.class).create());
        // First call should succeed.
        consumer.statusNotificationEvent(userId, event);
        // Second call (within the 5-second window) should throw an exception.
        Assertions.assertThrows(TooManyNotificationsException.class, () -> {
            consumer.statusNotificationEvent(userId, event);
        });
        verify(service, times(1)).send(userId, event.type(), event.message());
    }

    @Test
    void testStatusNotification_DifferentUsers_NotRateLimited() {
        String userId1 = UUID.randomUUID().toString();
        String userId2 = UUID.randomUUID().toString();
        NotificationEvent event = new NotificationEvent("Status", Instancio.of(String.class).create());

        consumer.statusNotificationEvent(userId1, event);
        consumer.statusNotificationEvent(userId2, event);

        verify(service, times(1)).send(userId1, event.type(), event.message());
        verify(service, times(1)).send(userId2, event.type(), event.message());
    }

    @Test
    void testStatusNotification_AfterWindowExpires() {
        String userId = UUID.randomUUID().toString();
        NotificationEvent event = new NotificationEvent("Status", Instancio.of(String.class).create());

        consumer.statusNotificationEvent(userId, event);
        // Wait until the rate limit window expires.
        await().atLeast(5, TimeUnit.SECONDS).untilAsserted(() ->
                Assertions.assertDoesNotThrow(() -> consumer.statusNotificationEvent(userId, event))
        );

        verify(service, times(2)).send(userId, event.type(), event.message());
    }

    @Test
    void testMarketingNotification_RateLimit() {
        String userId = UUID.randomUUID().toString();
        NotificationEvent event = new NotificationEvent("Marketing", Instancio.of(String.class).create());
        // First call should succeed.
        consumer.marketingNotificationEvent(userId, event);
        // Second call (within the 5-second window) should throw an exception.
        Assertions.assertThrows(TooManyNotificationsException.class, () -> {
            consumer.marketingNotificationEvent(userId, event);
        });
        verify(service, times(1)).send(userId, event.type(), event.message());
    }

    @Test
    void testNewsNotification_RateLimit() {
        String userId = UUID.randomUUID().toString();
        NotificationEvent event = new NotificationEvent("News", Instancio.of(String.class).create());
        // First call should succeed.
        consumer.newsNotificationEvent(userId, event);
        // Second call (within the 5-second window) should throw an exception.
        Assertions.assertThrows(TooManyNotificationsException.class, () -> {
            consumer.newsNotificationEvent(userId, event);
        });
        verify(service, times(1)).send(userId, event.type(), event.message());
    }
}