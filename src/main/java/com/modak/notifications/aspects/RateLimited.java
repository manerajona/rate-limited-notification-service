package com.modak.notifications.aspects;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.temporal.ChronoUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RateLimited {
    /**
     * A SpEL expression used to extract the key from method parameters.
     */
    String key();

    long windowValue() default 1;

    ChronoUnit windowUnit() default ChronoUnit.SECONDS;
}