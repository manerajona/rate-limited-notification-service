package com.modak.notifications.ports.input;

public record NotificationEvent(String type, String userId, String message) {
}