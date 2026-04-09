package com.authService.controller;

import com.authService.dto.ApiResponse;
import com.authService.dto.AuthResponse;
import com.authService.dto.LoginRequest;
import com.authService.services.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    //  LOGIN
    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        log.info("Login API called for {}", request.getUsername());
        return service.login(request.getUsername(), request.getPassword());
    }

    //  VALIDATE TOKEN (VERY IMPORTANT)
    @GetMapping("/validate")
    public boolean validateToken(@RequestParam String token) {
        log.info("Validate API called");
        return service.validateToken(token);
    }

    //  LOGOUT
    @PostMapping("/logout")
    public ApiResponse logout(
            @RequestHeader("Authorization") String header,
            @RequestHeader("X-User-Name") String username) {

        String token = header.substring(7);
        log.info("Logout API called for {}", username);

        return service.logout(token, username);
    }
}