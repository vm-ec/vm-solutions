package com.vmsolutions.health.dto;

import java.time.Instant;
import java.util.Map;

public record HealthCheckResponse(
        String status,
        String serviceName,
        Instant timestamp,
        Map<String, String> components
) {
}
