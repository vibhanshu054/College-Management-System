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

                // ================= AUTH =================
                .route("auth_swagger", r -> r.path("/api/auth/v3/api-docs")
                        .filters(f -> f.rewritePath("/api/auth/v3/api-docs", "/v3/api-docs"))
                        .uri("lb://AUTH-SERVICE"))

                .route("auth_api", r -> r.path("/api/auth/**")
                        .uri("lb://AUTH-SERVICE"))

                // ================= USER =================
                .route("user_swagger", r -> r.path("/api/users/v3/api-docs")
                        .filters(f -> f.rewritePath("/api/users/v3/api-docs", "/v3/api-docs"))
                        .uri("lb://USER-SERVICE"))

                .route("user_api", r -> r.path("/api/users/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://USER-SERVICE"))

                // ================= FACULTY =================
                .route("faculty_swagger", r -> r.path("/api/faculty/v3/api-docs")
                        .filters(f -> f.rewritePath("/api/faculty/v3/api-docs", "/v3/api-docs"))
                        .uri("lb://FACULTY-SERVICE"))

                .route("faculty_api", r -> r.path("/api/faculty/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://FACULTY-SERVICE"))

                // ================= LIBRARY =================
                .route("library_swagger", r -> r.path("/api/library/v3/api-docs")
                        .filters(f -> f.rewritePath("/api/library/v3/api-docs", "/v3/api-docs"))
                        .uri("lb://LIBRARY-SERVICE"))

                .route("library_api", r -> r.path("/api/library/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://LIBRARY-SERVICE"))

                // ================= STUDENT =================
                .route("student_swagger", r -> r.path("/api/students/v3/api-docs")
                        .filters(f -> f.rewritePath("/api/students/v3/api-docs", "/v3/api-docs"))
                        .uri("lb://STUDENT-SERVICE"))

                .route("student_api", r -> r.path("/api/students/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://STUDENT-SERVICE"))

                // ================= COURSE =================
                .route("course_swagger", r -> r.path("/api/courses/v3/api-docs")
                        .filters(f -> f.rewritePath("/api/courses/v3/api-docs", "/v3/api-docs"))
                        .uri("lb://COURSE-SERVICE"))

                .route("course_api", r -> r.path("/api/courses/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://COURSE-SERVICE"))

                // ================= DEPARTMENT (NEW) =================
                .route("department_swagger", r -> r.path("/api/departments/v3/api-docs")
                        .filters(f -> f.rewritePath("/api/departments/v3/api-docs", "/v3/api-docs"))
                        .uri("lb://DEPARTMENT-SERVICE"))

                .route("department_api", r -> r.path("/api/departments/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://DEPARTMENT-SERVICE"))

                // ================= ATTENDANCE (NEW) =================
                .route("attendance_swagger", r -> r.path("/api/attendance/v3/api-docs")
                        .filters(f -> f.rewritePath("/api/attendance/v3/api-docs", "/v3/api-docs"))
                        .uri("lb://ATTENDANCE-SERVICE"))

                .route("attendance_api", r -> r.path("/api/attendance/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://ATTENDANCE-SERVICE"))

                // ================= SUBJECT (NEW) =================
                .route("subject_swagger", r -> r.path("/api/subjects/v3/api-docs")
                        .filters(f -> f.rewritePath("/api/subjects/v3/api-docs", "/v3/api-docs"))
                        .uri("lb://SUBJECT-SERVICE"))

                .route("subject_api", r -> r.path("/api/subjects/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://SUBJECT-SERVICE"))

                // ================= DASHBOARD =================
                .route("dashboard_swagger", r -> r.path("/api/dashboard/v3/api-docs")
                        .filters(f -> f.rewritePath("/api/dashboard/v3/api-docs", "/v3/api-docs"))
                        .uri("lb://DASHBOARD-SERVICE"))

                .route("dashboard_api", r -> r.path("/api/dashboard/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://DASHBOARD-SERVICE"))

                .build();
    }
}