package com.userService.controller;

import com.userService.dto.UserDto;
import com.userService.enums.Role;
import com.userService.exception.ForbiddenException;
import com.userService.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "APIs for User Profile Management (Admin & Self-Service)")
public class UserController {

    private final UserService userService;

    // ============ ADMIN OPERATIONS ============

    @PostMapping
    @Operation(summary = "Create new user", description = "Admin only - Create STUDENT, FACULTY, LIBRARIAN, or ADMIN")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDTO, Authentication auth) {
        log.info("Create user request from: {}", auth.getName());

        // Verify ADMIN role
        if (!auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new ForbiddenException("Only ADMIN can create users");
        }

        UserDto createdUser = userService.createUser(userDTO, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Admin only - Retrieve all users with optional role filter")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<UserDto>> getAllUsers(
            @RequestParam(required = false) String role,
            Authentication auth) {
        log.info("Get all users request from: {}", auth.getName());

        if (role != null) {
            return ResponseEntity.ok(userService.getUsersByRole(Role.valueOf(role)));
        }
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Admin can get any user, others get their own profile")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id, Authentication auth) {
        log.info("Get user {} request from: {}", id, auth.getName());

        UserDto user = userService.getUserById(id);

        // Non-admin can only view their own profile
        if (!auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            if (!user.getEmail().equals(auth.getName())) {
                throw new ForbiddenException("You can only view your own profile");
            }
        }

        return ResponseEntity.ok(user);
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get user by email", description = "Retrieve user by email address")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email, Authentication auth) {
        log.info("Get user by email: {} request from: {}", email, auth.getName());
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @GetMapping("/university-id/{universityId}")
    @Operation(summary = "Get user by university ID", description = "Retrieve user by 12-digit university ID")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<UserDto> getUserByUniversityId(@PathVariable String universityId, Authentication auth) {
        log.info("Get user by universityId: {} request from: {}", universityId, auth.getName());
        return ResponseEntity.ok(userService.getUserByUniversityId(universityId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Admin can update any user, others update their own profile")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long id,
            @RequestBody UserDto userDTO,
            Authentication auth) {
        log.info("Update user {} request from: {}", id, auth.getName());

        Role role = Role.valueOf(auth.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .orElse("STUDENT"));

        UserDto updatedUser = userService.updateUser(id, userDTO, auth.getName(), role);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Admin only - Delete user from system")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id, Authentication auth) {
        log.info("Delete user {} request from: {}", id, auth.getName());

        if (!auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new ForbiddenException("Only ADMIN can delete users");
        }

        userService.deleteUser(id, auth.getName());
        return ResponseEntity.ok(Map.of("message", "User deleted successfully", "userId", String.valueOf(id)));
    }

    @PutMapping("/{id}/password")
    @Operation(summary = "Update password", description = "User can update their own password")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, String>> updatePassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> passwordData,
            Authentication auth) {
        log.info("Update password request for user {} from: {}", id, auth.getName());

        UserDto user = userService.getUserById(id);

        // Only allow updating own password
        if (!user.getEmail().equals(auth.getName()) &&
                !auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new ForbiddenException("You can only update your own password");
        }

        userService.updatePassword(
                id,
                passwordData.get("oldPassword"),
                passwordData.get("newPassword"),
                auth.getName()
        );

        return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
    }

    // ============ STATISTICS ENDPOINTS ============

    @GetMapping("/count/by-role/{role}")
    @Operation(summary = "Get user count by role", description = "Get total count of users with specific role")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, Object>> getUserCountByRole(@PathVariable String role) {
        log.info("Get user count for role: {}", role);

        long count = userService.getUserCountByRole(Role.valueOf(role));
        return ResponseEntity.ok(Map.of(
                "role", role,
                "count", count
        ));
    }

    @GetMapping("/count/by-role-and-dept/{role}/{department}")
    @Operation(summary = "Get user count by role and department", description = "Get count of users by role and department")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, Object>> getUserCountByRoleAndDept(
            @PathVariable String role,
            @PathVariable String department) {
        log.info("Get user count for role: {} and department: {}", role, department);

        long count = userService.getUserCountByRoleAndDepartment(Role.valueOf(role), department);
        return ResponseEntity.ok(Map.of(
                "role", role,
                "department", department,
                "count", count
        ));
    }
}