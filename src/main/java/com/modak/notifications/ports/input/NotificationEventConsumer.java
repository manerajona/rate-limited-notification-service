package com.modak.notifications.ports.input;

import com.modak.notifications.aspects.RateLimited;
import com.modak.notifications.core.NotificationService;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;

@Component
public class NotificationEventConsumer {

    private final NotificationService service;

    public NotificationEventConsumer(NotificationService service) {
        this.service = service;
    }

    @RateLimited(key = "#userId", windowValue = 5, windowUnit = ChronoUnit.SECONDS)
    public void statusNotificationEvent(String userId, NotificationEvent event) {
        service.send(userId, event.type(), event.message());
    }

    @RateLimited(key = "#userId", windowValue = 6, windowUnit = ChronoUnit.HOURS)
    public void marketingNotificationEvent(String userId, NotificationEvent event) {
        service.send(userId, event.type(), event.message());
    }

    @RateLimited(key = "#userId", windowValue = 1, windowUnit = ChronoUnit.DAYS)
    public void newsNotificationEvent(String userId, NotificationEvent event) {
        service.send(userId, event.type(), event.message());
    }
}