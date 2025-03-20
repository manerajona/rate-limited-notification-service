package com.modak.notifications.ports.output;

public interface EmailGateway {

    void send(String userId, String subject, String body);
}