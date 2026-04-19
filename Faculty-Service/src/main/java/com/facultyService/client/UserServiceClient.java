package com.facultyService.client;

import com.facultyService.config.FeignConfig;
import com.facultyService.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "USER-SERVICE")
public interface UserServiceClient {

    @PostMapping("/api/users")
    void createUser(
            @RequestBody UserDto dto,
            @RequestHeader("X-User-Name") String username,
            @RequestHeader("X-User-Role") String role
    );
    @PutMapping("/api/users/deactivate/{universityId}")
    void deactivateUser(@PathVariable String universityId);
}