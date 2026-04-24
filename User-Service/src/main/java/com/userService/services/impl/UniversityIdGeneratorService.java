package com.userService.services.impl;

import com.userService.entity.UserEntity;
import com.userService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UniversityIdGeneratorService {

    private final UserRepository userRepository;

    /**
     * Generates university ID based on role
     * Format: [PREFIX][USER_ID_PADDED_6_DIGITS]
     * Examples: STU000001, FAC000001, LIB000001, ADM000001
     *
     * This is the SINGLE SOURCE OF TRUTH for university ID generation
     */
    public String generateUniversityId(String role) {
        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException("Role is required for university ID generation");
        }

        String prefix = switch (role.trim().toUpperCase()) {
            case "STUDENT" -> "STU";
            case "FACULTY" -> "FAC";
            case "LIBRARIAN" -> "LIB";
            case "ADMIN" -> "ADM";
            default -> "USR";
        };

        // Get next sequence number
        long nextId = getNextSequenceForRole(role.toUpperCase());
        String universityId = prefix + String.format("%06d", nextId);

        log.info("Generated University ID: {} for role: {}", universityId, role);
        return universityId;
    }

    /**
     * Gets next available sequence number for a role
     * This ensures unique, sequential IDs
     */
    private long getNextSequenceForRole(String role) {
        String prefix = switch (role) {
            case "STUDENT" -> "STU";
            case "FACULTY" -> "FAC";
            case "LIBRARIAN" -> "LIB";
            case "ADMIN" -> "ADM";
            default -> "USR";
        };

        // Count existing users with this role + 1
        long count = userRepository.findByRoleAndActiveTrue(
                com.userService.enums.UserRole.valueOf(role)
        ).size() + 1;

        return count;
    }

    /**
     * Validates if a university ID matches expected format
     */
    public boolean isValidUniversityIdFormat(String universityId, String role) {
        if (universityId == null || universityId.isBlank()) {
            return false;
        }

        String prefix = switch (role.toUpperCase()) {
            case "STUDENT" -> "STU";
            case "FACULTY" -> "FAC";
            case "LIBRARIAN" -> "LIB";
            case "ADMIN" -> "ADM";
            default -> "USR";
        };

        return universityId.startsWith(prefix) && universityId.length() == 9;
    }
}