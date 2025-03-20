package com.modak.notifications.aspects;

import com.modak.notifications.common.TooManyNotificationsException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

@Aspect
@Component
public class RateLimitingAspect {
    private final Map<String, Instant> lastNotificationTime = new ConcurrentHashMap<>();

    @Around("@annotation(rateLimited)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, RateLimited rateLimited) throws Throwable {
        // Convert annotation values into a Duration.
        Duration window = Duration.of(rateLimited.windowValue(), rateLimited.windowUnit());

        // Build the composite key using the method signature and evaluated key.
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String key = evaluateKey(joinPoint, rateLimited, methodSignature);
        String compositeKey = methodSignature.getMethod().getName() + ":" + key;

        Instant lastSent = lastNotificationTime.get(compositeKey);
        Instant now = Instant.now();
        if (lastSent != null && now.isBefore(lastSent.plus(window))) {
            throw new TooManyNotificationsException();
        }
        // Update the last notification time and proceed.
        lastNotificationTime.put(compositeKey, now);
        return joinPoint.proceed();
    }

    private static String evaluateKey(ProceedingJoinPoint joinPoint, RateLimited rateLimited, MethodSignature methodSignature) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        // Expose each parameter as a variable to the SpEL context.
        String[] paramNames = methodSignature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        IntStream.range(0, args.length).forEach(i -> context.setVariable(paramNames[i], args[i]));

        String keyExpression = rateLimited.key();
        return new SpelExpressionParser().parseExpression(keyExpression).getValue(context, String.class);
    }
}