package com.library.client;

import com.library.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(name = "USER-SERVICE")
public interface UserServiceClient {

    @PostMapping("/api/users")
    void createUser(@RequestBody UserDto dto);

    @GetMapping("/api/users")
    List<Map<String, Object>> getAllUsers();
}