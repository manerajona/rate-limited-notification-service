package com.modak.notifications.core;

import com.modak.notifications.config.circuitbreaker.CircuitBreakerRegistry;
import com.modak.notifications.ports.output.EmailGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final EmailGateway emailGateway;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public NotificationServiceImpl(EmailGateway emailGateway, CircuitBreakerRegistry registry) {
        this.emailGateway = emailGateway;
        this.circuitBreakerRegistry = registry;
    }

    @Override
    public void send(String type, String userId, String message) {
        circuitBreakerRegistry.circuitBreakerOf(type, userId)
                .handle(() -> emailGateway.send(userId, message))
                .whenComplete((result, exception) -> {
                    if (exception != null) {
                        log.error("error sending message. [type={}, user={}]", type, userId);
                    }
                });
    }
}