package com.modak.notifications.ports.input;

public record NotificationEvent(String type, String message) {
}