package com.vmsolutions.health.service;

import com.vmsolutions.health.dto.HealthCheckRequest;
import com.vmsolutions.health.dto.HealthCheckResponse;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class HealthCheckService {

    public HealthCheckResponse check(HealthCheckRequest request) {
        Map<String, String> components = new LinkedHashMap<>();
        components.put("application", "UP");

        if (request.deepCheck()) {
            components.put("diskSpace", checkDiskSpace());
        }

        boolean allUp = components.values().stream().allMatch("UP"::equals);

        return new HealthCheckResponse(
                allUp ? "UP" : "DOWN",
                request.serviceName(),
                Instant.now(),
                components
        );
    }

    private String checkDiskSpace() {
        long freeBytes = new java.io.File(".").getUsableSpace();
        long minimumThreshold = 50L * 1024 * 1024; // 50 MB
        return freeBytes > minimumThreshold ? "UP" : "DOWN";
    }
}
