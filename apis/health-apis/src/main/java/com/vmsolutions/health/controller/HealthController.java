package com.vmsolutions.health.controller;

import com.vmsolutions.health.dto.HealthCheckRequest;
import com.vmsolutions.health.dto.HealthCheckResponse;
import com.vmsolutions.health.service.HealthCheckService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    private final HealthCheckService healthCheckService;

    public HealthController(HealthCheckService healthCheckService) {
        this.healthCheckService = healthCheckService;
    }

    @PostMapping(value = "/check", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HealthCheckResponse> check(@Valid @RequestBody HealthCheckRequest request) {
        HealthCheckResponse response = healthCheckService.check(request);
        HttpStatus status = "UP".equals(response.status()) ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
        return ResponseEntity.status(status).body(response);
    }
}
