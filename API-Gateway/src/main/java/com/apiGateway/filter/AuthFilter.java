package com.apiGateway.filter;

import com.apiGateway.util.JwtUtil;
import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.context.annotation.Bean;
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
        String method = exchange.getRequest().getMethod().name();

        log.info("Incoming request: {} {}", method, path);

        // ================= PUBLIC APIs =================
        if (path.startsWith("/api/auth") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-resources") ||
                path.startsWith("/webjars")) {

            return chain.filter(exchange);
        }

        // ================= INTERNAL SERVICE CALL =================
        if (exchange.getRequest().getHeaders().getFirst("X-Internal-Call") != null) {
            log.info("Internal call detected → skipping auth");
            return chain.filter(exchange);
        }

        // ================= TOKEN =================
        String header = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            return onError(exchange, "Missing or Invalid Token");
        }

        String token = header.substring(7);

        try {
            String username = jwtUtil.extractUsername(token);
            String role = jwtUtil.extractRole(token);
            String department = jwtUtil.extractDepartment(token);

            log.info("User: {}, Role: {}", username, role);

            return webClientBuilder.build()
                    .get()
                    .uri("lb://AUTH-SERVICE/api/auth/validate?token={token}", token)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .flatMap(valid -> {

                        if (Boolean.FALSE.equals(valid)) {
                            return onError(exchange, "Token expired or blacklisted");
                        }

                        // ================= ADMIN / SYSTEM =================
                        if ("ADMIN".equals(role) || "SYSTEM".equals(role)) {
                            return chain.filter(addHeaders(exchange, username, role, department));
                        }

                        // ================= STUDENT =================
                        if ("STUDENT".equals(role)) {

                            if (path.startsWith("/api/students") ||
                                    path.startsWith("/api/student") ||
                                    path.startsWith("/api/library") ||
                                    path.startsWith("/api/dashboard/student") ||
                                    path.startsWith("/api/attendance/student")||
                                    path.startsWith("/api/subjects")) {
                                if ((method.equals("DELETE") || method.equals("PUT"))
                                        && path.startsWith("/api/subjects")) {

                                    return onError(exchange, "Only admin can update/delete subjects");
                                }
                                //  Library → only GET
                                if (path.startsWith("/api/library") && !method.equals("GET")) {
                                    return onError(exchange, "Students can only view library data");
                                }

                                return chain.filter(addHeaders(exchange, username, role, department));
                            }

                            return onError(exchange, "Access Denied");
                        }

                        // ================= FACULTY =================
                        if ("FACULTY".equals(role)) {

                            if (path.startsWith("/api/faculty") ||
                                    path.startsWith("/api/students") ||
                                    path.startsWith("/api/courses") ||
                                    path.startsWith("/api/attendance") ||
                                    path.startsWith("/api/dashboard/faculty") ||
                                    path.startsWith("/api/library")) {

                                return chain.filter(addHeaders(exchange, username, role, department));
                            }

                            return onError(exchange, "Access Denied");
                        }

                        // ================= LIBRARIAN =================
                        if ("LIBRARIAN".equals(role)) {

                            if (path.startsWith("/api/library") ||
                                    path.startsWith("/api/dashboard/library")) {

                                return chain.filter(addHeaders(exchange, username, role, department));
                            }

                            return onError(exchange, "Access Denied");
                        }

                        return onError(exchange, "Unauthorized Role");
                    });

        } catch (Exception e) {
            log.error("JWT validation failed", e);
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

    private ServerWebExchange addHeaders(ServerWebExchange exchange,
                                         String username,
                                         String role,
                                         String department) {

        ServerHttpRequest request = exchange.getRequest().mutate()
                .header("X-User-Name", username)
                .header("X-User-Role", role)
                .header("X-User-Department", department != null ? department : "")
                .build();

        return exchange.mutate().request(request).build();
    }
    @Bean
    public RequestInterceptor interceptor() {
        return template -> {
            template.header("X-Internal-Call", "true");
        };
    }

    @Override
    public int getOrder() {
        return -1;
    }
}