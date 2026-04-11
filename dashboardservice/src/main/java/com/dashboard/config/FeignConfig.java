package com.collage.dashboard.config;


import feign.Logger;
import feign.Request;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {


    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }


    @Bean
    public Request.Options options() {
        return new Request.Options(
                5000,  // connect timeout (5 sec)
                5000   // read timeout (5 sec)
        );
    }


    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            // Example: Custom header
            requestTemplate.header("X-Service-Request", "Dashboard-Service");

        };
    }
}