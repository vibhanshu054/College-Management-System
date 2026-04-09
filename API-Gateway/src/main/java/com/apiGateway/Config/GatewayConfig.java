package com.apiGateway.Config;

import com.apiGateway.filter.AuthFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder, AuthFilter filter) {

        return builder.routes()

                //  AUTH SERVICE
                .route("auth_swagger", r -> r.path("/api/auth/v3/api-docs")
                        .filters(f -> f.rewritePath("/api/auth/v3/api-docs", "/v3/api-docs"))
                        .uri("lb://Auth-Service"))//stop gateway to modify request paths in a way that broke the /v3/api-docs endpoint

                .route("auth_api", r -> r.path("/api/auth/**")
                        .uri("lb://Auth-Service"))

                //  USER SERVICE
                .route("user_swagger", r -> r.path("/api/users/v3/api-docs")
                        .filters(f -> f.rewritePath("/api/users/v3/api-docs", "/v3/api-docs"))
                        .uri("lb://User-Service"))

                .route("user_api", r -> r.path("/api/users/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://User-Service"))


                //  LIBRARY SERVICE
                .route("library_swagger", r -> r.path("/api/library/v3/api-docs")
                        .filters(f -> f.rewritePath("/api/library/v3/api-docs", "/v3/api-docs"))
                        .uri("lb://Library-Service"))

                .route("library_api", r -> r.path("/api/library/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://Library-Service"))


                //  STUDENT-SERVICE
                .route("student_swagger", r -> r.path("/api/students/v3/api-docs")
                        .filters(f -> f.rewritePath("/api/students/v3/api-docs", "/v3/api-docs"))
                        .uri("lb://Student-Service"))

                .route("student_api", r -> r.path("/api/students/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://Student-Service"))

                //  COURSE-SERVICE
                .route("courses_swagger", r -> r.path("/api/courses/v3/api-docs")
                        .filters(f -> f.rewritePath("/api/courses/v3/api-docs", "/v3/api-docs"))
                        .uri("lb://Course-Service"))

                .route("courses_api", r -> r.path("/api/courses/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://Course-Service"))

                .build();
    }
}