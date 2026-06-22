package com.vmsolutions.health.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "health.security.api-key.value=test-secret-key")
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void rejectsRequestWithoutApiKey() throws Exception {
        mockMvc.perform(post("/api/v1/health/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"serviceName\":\"orders-service\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rejectsRequestWithWrongApiKey() throws Exception {
        mockMvc.perform(post("/api/v1/health/check")
                        .header("X-API-KEY", "wrong-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"serviceName\":\"orders-service\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void acceptsRequestWithValidApiKey() throws Exception {
        mockMvc.perform(post("/api/v1/health/check")
                        .header("X-API-KEY", "test-secret-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"serviceName\":\"orders-service\",\"deepCheck\":true}"))
                .andExpect(status().isOk());
    }

    @Test
    void rejectsInvalidPayload() throws Exception {
        mockMvc.perform(post("/api/v1/health/check")
                        .header("X-API-KEY", "test-secret-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"serviceName\":\"\"}"))
                .andExpect(status().isBadRequest());
    }
}
