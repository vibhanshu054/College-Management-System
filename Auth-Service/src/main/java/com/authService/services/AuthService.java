package com.authService.services;

import com.authService.dto.ApiResponse;
import com.authService.dto.AuthResponse;
import com.authService.dto.LoginRequest;
import jakarta.validation.Valid;


public interface AuthService {
    boolean validateToken(String token);

    AuthResponse login(String username, String password);
    ApiResponse logout(String token, String username);

}
