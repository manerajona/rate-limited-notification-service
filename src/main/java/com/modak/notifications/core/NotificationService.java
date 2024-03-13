package com.modak.notifications.core;

public interface NotificationService {

    void send(String type, String userId, String message);
}