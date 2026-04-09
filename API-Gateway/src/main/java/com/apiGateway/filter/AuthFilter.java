package com.apiGateway.filter;

import com.apiGateway.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthFilter implements GatewayFilter, Ordered {

    private final JwtUtil jwtUtil;
    private final WebClient.Builder webClientBuilder;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        exchange.getRequest().getMethod();
        String method = exchange.getRequest().getMethod().name();

        log.info("Incoming request: {} {}", method, path);

        // Allow auth endpoints
        if (path.contains("/api/auth/login") ||
                path.contains("/api/auth/logout") ||
                path.contains("/api/auth/validate") ||
                path.contains("/v3/api-docs") ||
                path.contains("/swagger-ui") ||
                path.contains("/swagger-resources") ||
                path.contains("/webjars")) {

            log.info("Swagger/Auth endpoint accessed, skipping filter");
            return chain.filter(exchange);
        }
        // Extract token
        String header = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            log.error("Authorization header missing or invalid");
            return onError(exchange, "Missing or Invalid Token");
        }

        String token = header.substring(7);

        try {
            String username = jwtUtil.extractUsername(token);
            String role = jwtUtil.extractRole(token);

            log.info("Token validated. User: {}, Role: {}", username, role);

            //  Reactive blacklist check (NO block)
            return webClientBuilder.build()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("http")
                            .host("AUTH-SERVICE")
                            .path("/api/auth/validate")
                            .queryParam("token", token)
                            .build())
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .flatMap(isBlacklisted -> {

                        if (Boolean.FALSE.equals(isBlacklisted)) {
                            log.error("Invalid or blacklisted token for user {}", username);
                            return onError(exchange, "You are logged out. Please login again.");
                        }

                        log.info("Token is valid and not blacklisted");

                        //  ADMIN → Full access
                        if ("ADMIN".equals(role)) {
                            log.info("ADMIN access granted");
                            ServerHttpRequest request = exchange.getRequest().mutate()
                                    .header("X-User-Name", username)
                                    .build();
                            return chain.filter(exchange.mutate().request(request).build());
                        }

                        //  CREATE USER → Only ADMIN
                        if (path.equals("/api/users") && method.equals("POST")) {
                            if (!"ADMIN".equals(role)) {
                                log.error("Access denied: Only ADMIN can create users");
                                return onError(exchange, "Only ADMIN can create users");
                            }
                        }

                        //  VIEW ALL USERS → Only ADMIN
                        if (path.startsWith("/api/users/all")) {
                            if (!"ADMIN".equals(role)) {
                                log.error("Access denied: Only ADMIN can view all users");
                                return onError(exchange, "Only ADMIN allowed");
                            }
                        }

                        //  ADMIN DASHBOARD
                        if (path.startsWith("/api/dashboard/admin")) {
                            if (!"ADMIN".equals(role)) {
                                return onError(exchange, "Only ADMIN allowed");
                            }
                        }

                        //  FACULTY DASHBOARD
                        if (path.startsWith("/api/dashboard/faculty")) {
                            if (!"FACULTY".equals(role) && !"ADMIN".equals(role)) {
                                return onError(exchange, "Access Denied");
                            }
                        }

                        //  FACULTY → Courses + Students
                        if (path.startsWith("/api/courses") || path.startsWith("/api/students")) {
                            if (!"FACULTY".equals(role) && !"ADMIN".equals(role)) {
                                log.error("Access denied: Only FACULTY or ADMIN allowed");
                                return onError(exchange, "Access Denied");
                            }
                        }

                        //  LIBRARIAN DASHBOARD
                        if (path.startsWith("/api/dashboard/library")) {
                            if (!"LIBRARIAN".equals(role) && !"ADMIN".equals(role)) {
                                return onError(exchange, "Access Denied");
                            }
                        }

                        //  LIBRARIAN → Library only
                        if (path.startsWith("/api/library")) {
                            if (!"LIBRARIAN".equals(role) && !"ADMIN".equals(role)) {
                                log.error("Access denied: Only LIBRARIAN or ADMIN allowed");
                                return onError(exchange, "Access Denied");
                            }
                        }

                        //  STUDENT → Profile + own books (FIXED)
                        if (path.startsWith("/api/users/profile") ||
                                path.startsWith("/api/library/my-books")) {

                            if (!"STUDENT".equals(role) && !"ADMIN".equals(role)) {
                                log.error("Access denied: Only STUDENT or ADMIN allowed");
                                return onError(exchange, "Access Denied");
                            }
                        }

                        //  Pass username downstream
                        ServerHttpRequest request = exchange.getRequest().mutate()
                                .header("X-User-Name", username)
                                .build();

                        log.info("Request authorized for user {}", username);

                        return chain.filter(exchange.mutate().request(request).build());
                    });

        } catch (Exception e) {
            log.error("JWT validation failed: {}", e.getMessage());
            return onError(exchange, "Invalid Token");
        }
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message) {

        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String jsonBody = "{\"status\":401,\"message\":\"" + message + "\"}";

        byte[] bytes = jsonBody.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);

        log.error("Unauthorized access: {}", message);

        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}