package com.attendance_Service.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI attendanceAPI() {
        return new OpenAPI()
                .addServersItem(new Server().url("http://localhost:8080")) // change if needed
                .info(new Info()
                        .title("Attendance Service API")
                        .version("v1")
                        .description("Attendance Management APIs"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("Authorization")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                );
    }

    @Bean
    public GroupedOpenApi attendanceGroup() {
        return GroupedOpenApi.builder()
                .group("attendance-v1")
                .pathsToMatch("/attendance/**")   //  FIXED
                .build();
    }
}