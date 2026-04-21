package com.dashboard.config;

import com.dashboard.exception.FeignException;
import feign.Logger;
import feign.Request;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class FeignConfig {

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL; // Full request/response logging
    }

    @Bean
    public Request.Options options() {
        // connectTimeout: 5000ms, readTimeout: 5000ms
        return new Request.Options(5000, 5000);
    }

    /**
     * ERROR DECODER - Handle Feign errors properly
     */
    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            log.error("Feign Error - Method: {}, Status: {}, Reason: {}",
                    methodKey, response.status(), response.reason());

            if (response.status() == 404) {
                throw new FeignException("Resource not found from upstream service");
            } else if (response.status() == 403 || response.status() == 401) {
                throw new FeignException("Authentication/Authorization failed");
            } else if (response.status() >= 500) {
                throw new FeignException("Upstream service error: " + response.reason());
            } else if (response.status() >= 400) {
                throw new FeignException("Bad request to upstream service");
            }
            throw new FeignException("Unknown error from upstream service");
        };
    }

    /**
     * Forward Authorization token + custom header
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            // Add request ID for tracing
            String requestId = java.util.UUID.randomUUID().toString();
            requestTemplate.header("X-Request-ID", requestId);

            // Custom header
            requestTemplate.header("X-Service-Request", "Dashboard-Service");
            requestTemplate.header("X-Service-Timestamp", System.currentTimeMillis() + "");

            // Forward JWT token (IMPORTANT)
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attrs != null) {
                String token = attrs.getRequest().getHeader("Authorization");
                if (token != null && !token.isEmpty()) {
                    requestTemplate.header("Authorization", token);
                    log.debug("JWT token forwarded for request: {}", requestId);
                } else {
                    log.warn("No Authorization header found for request: {}", requestId);
                }
            } else {
                log.warn("Request context is null - cannot forward JWT token");
            }
        };
    }
}