package com.student.client;

import com.student.dto.ApiResponse;
import com.student.dto.UserDto;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "USER-SERVICE")
public interface UserServiceClient {

    @GetMapping("/api/users/university/{universityId}")
    ResponseEntity<ApiResponse> getUserByUniversityId(@PathVariable String universityId);

    @PostMapping("/api/users")
    ResponseEntity<ApiResponse> createUser(@RequestBody UserDto dto);

    @PutMapping("/api/users/university/{universityId}")
    ResponseEntity<ApiResponse> updateUserByUniversityId(
            @PathVariable String universityId,
            @RequestBody UserDto dto
    );

    @PutMapping("/api/users/university/{universityId}/password")
    ResponseEntity<ApiResponse> updatePassword(
            @PathVariable String universityId,
            @RequestBody Map<String, String> password
    );

    @DeleteMapping("/api/users/university/{universityId}")
    ResponseEntity<ApiResponse> deleteUser(@PathVariable String universityId);
}