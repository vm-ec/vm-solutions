package com.vmsolutions.health.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "health.security.api-key")
public class ApiKeyProperties {

    /** Name of the HTTP header carrying the API key. */
    private String header = "X-API-KEY";

    /** Expected API key value, supplied via environment/secret manager. */
    private String value;

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
