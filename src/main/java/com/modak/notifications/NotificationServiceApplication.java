package com.modak.notifications;

import com.modak.notifications.common.TooManyNotificationsException;
import com.modak.notifications.ports.input.NotificationEvent;
import com.modak.notifications.ports.input.NotificationEventConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

@SpringBootApplication
@EnableAspectJAutoProxy
public class NotificationServiceApplication {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceApplication.class);

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(NotificationServiceApplication.class, args);
        NotificationEventConsumer notificationEventConsumer = ctx.getBean(NotificationEventConsumer.class);

        UUID[] users = {UUID.randomUUID(), UUID.randomUUID()};
        String[] notificationTypes = {"News", "Marketing", "Status"};

        // Map each notification type to its corresponding consumer method using method references.
        Map<String, BiConsumer<String, NotificationEvent>> notificationMap = new HashMap<>();
        notificationMap.put("News", notificationEventConsumer::newsNotificationEvent);
        notificationMap.put("Marketing", notificationEventConsumer::marketingNotificationEvent);
        notificationMap.put("Status", notificationEventConsumer::statusNotificationEvent);

        // Simulate sending notifications for each user and type.
        for (UUID userId : users) {
            String user = userId.toString();
            for (String type : notificationTypes) {
                BiConsumer<String, NotificationEvent> sendNotification = notificationMap.get(type);
                sendNotificationTwice(sendNotification, user, type);
            }
        }
    }


    private static void sendNotificationTwice(BiConsumer<String, NotificationEvent> sendNotification,
                                              String userId, String notificationType) {
        try {
            NotificationEvent event = new NotificationEvent(notificationType, notificationType + " notification");
            sendNotification.accept(userId, event);
            sendNotification.accept(userId, event);
        } catch (TooManyNotificationsException e) {
            log.error("Rate limit exceeded for {}: {}", notificationType, e.getMessage(), e);
        }
    }
}