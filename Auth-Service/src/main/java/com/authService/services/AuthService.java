package com.authService.services;

import com.authService.dto.ApiResponse;
import com.authService.dto.AuthResponse;


public interface AuthService {
    boolean validateToken(String token);

    AuthResponse login(String username, String password);
    ApiResponse logout(String token, String username);
}
