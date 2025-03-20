package com.modak.notifications.core;

import com.modak.notifications.ports.output.EmailGateway;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final EmailGateway emailGateway;

    public NotificationServiceImpl(EmailGateway emailGateway) {
        this.emailGateway = emailGateway;
    }

    @Override
    public void send(String userId, String type, String message) {
        emailGateway.send(userId, type, message);
    }
}