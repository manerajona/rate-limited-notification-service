package com.modak.notifications.ports.input;

import com.modak.notifications.core.NotificationService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class NotificationEventConsumer implements ApplicationRunner {

    private final NotificationService service;

    public static final Set<NotificationEvent> events;

    static {
        events = Set.of(
                new NotificationEvent("news", "user", "news 1"),
                new NotificationEvent("news", "user", "news 2"),
                new NotificationEvent("news", "user", "news 3"),
                new NotificationEvent("news", "another user", "news 1"),
                new NotificationEvent("update", "user", "update 1"),
                new NotificationEvent("update", "user", "update 2"));
    }

    public NotificationEventConsumer(NotificationService service) {
        this.service = service;
    }

    @Override
    public void run(ApplicationArguments args) throws InterruptedException {
        for (NotificationEvent event : events) {
            service.send(event.type(), event.userId(), event.message());
            Thread.sleep(1000);
        }
    }
}