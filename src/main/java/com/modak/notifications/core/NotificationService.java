package com.modak.notifications.core;

public interface NotificationService {

    void send(String userId, String type, String message);
}