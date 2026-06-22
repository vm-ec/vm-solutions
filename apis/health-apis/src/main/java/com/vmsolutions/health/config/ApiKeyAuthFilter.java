package com.vmsolutions.health.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

/**
 * Authenticates requests via a shared-secret API key header.
 * Comparison uses MessageDigest.isEqual to avoid timing side-channels.
 */
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(ApiKeyAuthFilter.class);

    private final ApiKeyProperties apiKeyProperties;

    public ApiKeyAuthFilter(ApiKeyProperties apiKeyProperties) {
        this.apiKeyProperties = apiKeyProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {
        String providedKey = request.getHeader(apiKeyProperties.getHeader());
        String expectedKey = apiKeyProperties.getValue();

        if (isValid(providedKey, expectedKey)) {
            var authentication = new ApiKeyAuthenticationToken(List.of(new SimpleGrantedAuthority("ROLE_HEALTH_CLIENT")));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
            return;
        }

        log.warn("Rejected request to {} {} - missing or invalid API key", request.getMethod(), request.getRequestURI());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"error\":\"unauthorized\",\"message\":\"Missing or invalid API key\"}");
    }

    private boolean isValid(String providedKey, String expectedKey) {
        if (providedKey == null || expectedKey == null || expectedKey.isBlank()) {
            return false;
        }
        byte[] provided = providedKey.getBytes(StandardCharsets.UTF_8);
        byte[] expected = expectedKey.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(provided, expected);
    }

    private static final class ApiKeyAuthenticationToken extends AbstractAuthenticationToken {
        ApiKeyAuthenticationToken(List<SimpleGrantedAuthority> authorities) {
            super(authorities);
            setAuthenticated(true);
        }

        @Override
        public Object getCredentials() {
            return null;
        }

        @Override
        public Object getPrincipal() {
            return "health-api-client";
        }
    }
}
