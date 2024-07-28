package com.infinitynet.server.components;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

@Component
public class HealthCheck implements HealthIndicator {

    @Override
    public Health health() {
        try {
            String hostName = InetAddress.getLocalHost().getHostName();
            return Health.up().withDetail("hostName", hostName).build();

        } catch (Exception e) {
            return Health.down().withDetail("error", e.getMessage()).build();
        }
    }

}
