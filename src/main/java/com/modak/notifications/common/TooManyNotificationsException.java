package com.modak.notifications.common;

public class TooManyNotificationsException extends RuntimeException {
    public TooManyNotificationsException() {
        super("Rate limit hit");
    }
}
