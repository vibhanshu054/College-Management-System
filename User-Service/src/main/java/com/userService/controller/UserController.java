package com.userService.controller;

import com.userService.dto.UpdatePasswordDto;
import com.userService.dto.UserDto;
import com.userService.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {


    private final UserService service;

    //Own Profile
    @GetMapping("/profile")
    public ResponseEntity<UserDto> getMyProfile(
            @RequestHeader("X-User-Name") String username) {

        log.info("Fetching profile {}", username);

        return ResponseEntity.ok(service.findByUsername(username));
    }

    //Create user
    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto dto) {

        log.info("Creating User");

        return ResponseEntity.ok(service.save(dto));
    }

    //Fetch All User
    @GetMapping("/all")
    public ResponseEntity<List<UserDto>> getAllUser() {

        log.info("Fetching all User");

        return ResponseEntity.ok(
                service.getAllUser()
                        .stream()
                        .map(e -> new UserDto(
                                e.getId(),
                                e.getUsername(),
                                e.getEmail(),
                                e.getPassword(),
                                e.getRole()))
                        .toList()
        );
    }

    @GetMapping("/one-user")
    public ResponseEntity<UserDto> getUser(@RequestParam Long id) {

        log.info("Fetching User {}", id);

        return ResponseEntity.ok(service.getUser(id));
    }

    //Login
    @GetMapping("/login")
    public UserDto login(@RequestParam String username) {

        log.info("Login request {}", username);

        return service.findByUsername(username);
    }

    //Forget/Update Password
    @PostMapping("/update-password")
    public String updatePassword(@RequestBody UpdatePasswordDto dto) {
        return service.updatePassword(dto);
    }

    //By-Email
    @GetMapping("/by-email")
    public UserDto getByEmail(@RequestParam String email) {
        return service.findByEmail(email);
    }
}