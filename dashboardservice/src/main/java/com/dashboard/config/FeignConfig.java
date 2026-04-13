package com.dashboard.config;

import feign.Logger;
import feign.Request;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfig {

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public Request.Options options() {
        return new Request.Options(5000, 5000);
    }

    /**
     * Forward Authorization token + custom header
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {

            // Custom header
            requestTemplate.header("X-Service-Request", "Dashboard-Service");

            //  Forward JWT token (IMPORTANT)
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attrs != null) {
                String token = attrs.getRequest().getHeader("Authorization");
                if (token != null) {
                    requestTemplate.header("Authorization", token);
                }
            }
        };
    }
}