package com.infinitynet.server.components;

import com.infinitynet.server.annotations.RateLimit;
import com.infinitynet.server.enums.RateLimitKeyType;
import com.infinitynet.server.exceptions.authentication.AuthenticationErrorCode;
import com.infinitynet.server.exceptions.authentication.AuthenticationException;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.infinitynet.server.exceptions.authentication.AuthenticationErrorCode.RATE_LIMIT_EXCEEDED;
import static com.infinitynet.server.exceptions.authentication.AuthenticationErrorCode.TOKEN_MISSING;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Aspect
@Component
public class RateLimitAspect {

    private final HttpServletRequest request; // Inject HttpServletRequest

    public RateLimitAspect(HttpServletRequest request) {
        this.request = request;
    }

    private Map<String, Map<String, AtomicInteger>> requestCounts = new ConcurrentHashMap<>();

    @Before("@annotation(rateLimit) && execution(* *(..))")
    public void rateLimit(RateLimit rateLimit) throws ParseException {
        // Get information from the annotation
        int limit = rateLimit.limit();
        int timeWindow = rateLimit.timeWindow();
        RateLimitKeyType[] rateLimitKeyType = rateLimit.limitKeyTypes();

        for (RateLimitKeyType keyType : rateLimitKeyType) {
            String key = generateKey(keyType);

            requestCounts.putIfAbsent(key, new ConcurrentHashMap<>());
            Map<String, AtomicInteger> timeWindowMap = requestCounts.get(key);

            // Check if the request count exceeds the limit
            AtomicInteger count = timeWindowMap.computeIfAbsent(
                    String.valueOf(System.currentTimeMillis() / 1000),
                    k -> new AtomicInteger(0)
            );

            if (count.incrementAndGet() > limit) {
                switch (keyType) {
                    case BY_TOKEN:
                        throw new AuthenticationException(RATE_LIMIT_EXCEEDED, TOO_MANY_REQUESTS);

                    default:
                        throw new AuthenticationException(
                                AuthenticationErrorCode.TOO_MANY_REQUESTS,
                                HttpStatus.TOO_MANY_REQUESTS
                        );
                }
            }

            // Remove expired time windows
            timeWindowMap.entrySet().removeIf(
                    entry -> (System.currentTimeMillis() / 1000) - Long.parseLong(entry.getKey()) > timeWindow);
        }
    }

    private String generateKey(RateLimitKeyType rateLimitKeyType) throws ParseException {
        switch (rateLimitKeyType) {
            case BY_TOKEN:
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                    SignedJWT signedJWT = SignedJWT.parse(token);
                    return signedJWT.getJWTClaimsSet().getJWTID();  // This JWT ID will be used as the key for rate limiting

                } else
                    throw new AuthenticationException(TOKEN_MISSING, UNAUTHORIZED);

            default:
                String ipAddress = request.getRemoteAddr();
                String xForwardedForHeader = request.getHeader("X-Forwarded-For");
                if (xForwardedForHeader != null) {
                    ipAddress = xForwardedForHeader.split(",")[0];
                }
                return ipAddress;  // This IP address will be used as the key for rate limiting
        }
    }

}
