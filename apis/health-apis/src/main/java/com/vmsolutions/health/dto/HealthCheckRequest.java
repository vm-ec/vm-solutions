package com.vmsolutions.health.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for a health check probe.
 * Allows the caller to identify itself and optionally request a deep check.
 */
public record HealthCheckRequest(

        @NotBlank(message = "serviceName is required")
        @Size(max = 100, message = "serviceName must be at most 100 characters")
        String serviceName,

        boolean deepCheck
) {
}
