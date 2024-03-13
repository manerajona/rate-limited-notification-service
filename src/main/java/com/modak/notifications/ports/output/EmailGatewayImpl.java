package com.modak.notifications.ports.output;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmailGatewayImpl implements EmailGateway {

    private static final Logger log = LoggerFactory.getLogger(EmailGatewayImpl.class);

    @Override
    public void send(String userId, String message) {
        log.debug("sending message to user {}", userId);
    }
}
