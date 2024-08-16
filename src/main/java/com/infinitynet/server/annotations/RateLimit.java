package com.infinitynet.server.annotations;

import com.infinitynet.server.enums.RateLimitKeyType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    int limit() default 3;

    int timeWindow() default 60; // Seconds

    RateLimitKeyType[] limitKeyTypes() default { RateLimitKeyType.BY_IP };

}
