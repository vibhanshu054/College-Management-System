package com.userService.controller;

import com.userService.dto.ApiResponse;
import com.userService.dto.LoginRequest;
import com.userService.dto.UpdatePasswordDto;
import com.userService.dto.UserDto;
import com.userService.exception.ForbiddenException;
import com.userService.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management")
public class UserController {

    private final UserService userService;


    @PutMapping("/university/{universityId}/password")
    public ResponseEntity<ApiResponse> updatePasswordByUniversityId(
            @PathVariable String universityId,
            @RequestBody Map<String, String> passwordData,
            @RequestHeader(value = "X-User-Name", required = false) String username
    ) {

        username = getUsername(username);

        log.info("UPDATE PASSWORD BY UNIVERSITY ID | universityId={} | requestedBy={}",
                universityId, username);

        userService.updatePasswordByUniversityId(
                universityId,
                passwordData.get("oldPassword"),
                passwordData.get("newPassword"),
                username
        );

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Password updated successfully")
                        .build()
        );
    }



    @PutMapping("/university/{universityId}")
    public ResponseEntity<ApiResponse> updateUserByUniversityId(
            @PathVariable String universityId,
            @RequestBody UserDto dto,
            @RequestHeader(value = "X-User-Name", required = false) String username
    ) {

        username = getUsername(username);

        log.info("UPDATE USER BY UNIVERSITY ID | universityId={} | requestedBy={}",
                universityId, username);

        UserDto updated = userService.updateUserByUniversityId(universityId, dto, username);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("User updated successfully")
                        .data(updated)
                        .build()
        );
    }
    @DeleteMapping("/university/{universityId}")
    public ResponseEntity<ApiResponse> deleteUserByUniversityId(
            @PathVariable String universityId,
            @RequestHeader(value = "X-User-Name", required = false) String username,
            @RequestHeader(value = "X-User-Role", required = false) String role
    ) {

        username = getUsername(username);

        log.info("DELETE USER BY UNIVERSITY ID | universityId={} | requestedBy={} | role={}",
                universityId, username, role);

        userService.deleteUserByUniversityId(universityId, username, role);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("User deleted successfully")
                        .reason("Soft delete applied")
                        .build()
        );
    }
    // ================= HELPER =================
    private String getUsername(String username) {
        return (username != null && !username.isBlank()) ? username : "SYSTEM";
    }

    private boolean isAdmin(String role) {
        return "ADMIN".equals(role) || "SYSTEM".equals(role);
    }

    // ================= CREATE USER =================
    @PostMapping
    public ResponseEntity<ApiResponse> createUser(
            @Valid @RequestBody UserDto userDTO,
            @RequestHeader(value = "X-User-Name", required = false) String username,
            @RequestHeader(value = "X-User-Role", required = false) String role
    ) {

        username = getUsername(username);

        log.info("CREATE USER | requestedBy={} | role={}", username, role);

        UserDto created = userService.createUser(userDTO, username, role);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.CREATED.value())
                        .message("User created successfully")
                        .username(created.getEmail())
                        .data(created)
                        .build()
        );
    }

    // ================= GET ALL USERS =================
    @GetMapping
    public ResponseEntity<ApiResponse> getAllUsers(
            @RequestParam(required = false) String role,
            @RequestHeader(value = "X-User-Name", required = false) String username,
            @RequestHeader(value = "X-User-Role", required = false) String userRole
    ) {

        username = getUsername(username);

        log.info("GET ALL USERS | requestedBy={} | role={}", username, userRole);

        if (!isAdmin(userRole)) {
            throw new ForbiddenException("Only ADMIN can view all users");
        }

        List<UserDto> users = (role != null && !role.isBlank())
                ? userService.getUsersByRole(role)
                : userService.getAllUsers(userRole);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Users fetched successfully")
                        .data(users)
                        .build()
        );
    }
// Get user by university ID
    @GetMapping("/university/{universityId}")
    public ResponseEntity<ApiResponse> getUserByUniversityId(
            @PathVariable String universityId,
            @RequestHeader(value = "X-User-Name", required = false) String username
    ) {
        username = getUsername(username);

        log.info("GET USER BY UNIVERSITY ID | universityId={} | requestedBy={}", universityId, username);

        UserDto user = userService.getUserByUniversityId(universityId);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("User fetched successfully")
                        .username(user.getEmail())
                        .data(user)
                        .build()
        );
    }
    // ================= GET USER BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getUserById(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Name", required = false) String username
    ) {

        username = getUsername(username);

        log.info("GET USER | id={} | requestedBy={}", id, username);

        UserDto user = userService.getUserById(id);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("User fetched successfully")
                        .username(user.getEmail())
                        .data(user)
                        .build()
        );
    }

    // ================= DELETE USER =================
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteUser(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Name", required = false) String username,
            @RequestHeader(value = "X-User-Role", required = false) String role
    ) {

        username = getUsername(username);

        log.info("DELETE USER | id={} | requestedBy={} | role={}", id, username, role);


        userService.deleteUser(id, username, role);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("User deleted successfully")
                        .reason("Soft delete applied")
                        .build()
        );
    }

    // ================= UPDATE PASSWORD =================
    @PutMapping("/{id}/password")
    public ResponseEntity<ApiResponse> updatePassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> passwordData,
            @RequestHeader(value = "X-User-Name", required = false) String username
    ) {

        username = getUsername(username);

        log.info("UPDATE PASSWORD | id={} | requestedBy={}", id, username);

        userService.updatePassword(
                id,
                passwordData.get("oldPassword"),
                passwordData.get("newPassword"),
                username
        );

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Password updated successfully")
                        .username(username)
                        .build()
        );
    }


    // ================= UPDATE USER =================
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateUser(
            @PathVariable Long id,
            @RequestBody UserDto dto,
            @RequestHeader(value = "X-User-Name", required = false) String username,
            @RequestHeader(value = "X-User-Role", required = false) String role
    ) {

        username = getUsername(username);

        log.info("UPDATE USER | id={} | requestedBy={} | role={}", id, username, role);


        UserDto updated = userService.updateUser(
                id,
                dto,
                username,
                null // role not needed here
        );

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("User updated successfully")
                        .data(updated)
                        .build()
        );

    }

    // ================= VERIFY =================
    @PostMapping("/verify")
    public ResponseEntity<UserDto> verifyCredentials(@RequestBody LoginRequest request) {

        log.info("VERIFY USER | email={}", request.getEmail());

        UserDto user = userService.verifyCredentials(
                request.getEmail(),
                request.getPassword()
        );

        return ResponseEntity.ok(user);
    }

    // ================= INTERNAL =================
    @GetMapping("/internal/by-email")
    public ResponseEntity<ApiResponse> lookupUserByEmail(@RequestParam String email) {

        UserDto user = userService.getUserByEmail(email);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("User fetched successfully")
                        .data(user)
                        .build()
        );
    }
    @PutMapping("/internal/reset-password")
    public ResponseEntity<ApiResponse> resetPasswordFromForgotFlow(
            @RequestBody UpdatePasswordDto dto  //
    ) {
        log.info("RESET PASSWORD FROM FORGOT FLOW | username={}", dto.getUsername());

        userService.resetPasswordByUsername(dto.getUsername(), dto.getNewPassword());

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Password reset successfully")
                        .username(dto.getUsername())
                        .build()
        );
    }
}