package com.userService.services.impl;
import com.userService.dto.UserDto;
import com.userService.entity.UserEntity;
import com.userService.enums.Role;
import com.userService.exception.*;
import com.userService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleServiceIntegration roleServiceIntegration;

    /**
     * Create a new user (ADMIN only operation)
     */
    public UserDto createUser(UserDto userDto, String createdBy) {
        log.info("Creating user with email: {} and role: {}", userDto.getEmail(), userDto.getRole());

        // Check if user already exists
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            log.warn("Duplicate email attempted: {}", userDto.getEmail());
            throw new DuplicateResourceException("Email already exists: " + userDto.getEmail());
        }

        if (userRepository.findByUniversityId(userDto.getUniversityId()).isPresent()) {
            log.warn("Duplicate university ID attempted: {}", userDto.getUniversityId());
            throw new DuplicateResourceException("University ID already exists: " + userDto.getUniversityId());
        }

        UserEntity user = modelMapper.map(userDto, UserEntity.class);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setCreatedBy(createdBy);
        user.setUpdatedBy(createdBy);
        user.setActive(true);

        UserEntity savedUser = userRepository.save(user);
        log.info("User created successfully with ID: {} and email: {}", savedUser.getId(), savedUser.getEmail());

        // Cascade creation to specific services
        cascadeUserCreation(savedUser);

        return modelMapper.map(savedUser, UserDto.class);
    }

    /**
     * Cascade user creation to specific services
     */
    private void cascadeUserCreation(UserEntity user) {
        log.debug("Cascading user creation for role: {}", user.getRole());

        switch (user.getRole()) {
            case STUDENT:
                Long studentServiceId = roleServiceIntegration.createStudentInStudentService(user);
                user.setStudentServiceId(studentServiceId);
                log.info("Student created in Student-Service with ID: {}", studentServiceId);
                break;

            case FACULTY:
                Long facultyServiceId = roleServiceIntegration.createFacultyInFacultyService(user);
                user.setFacultyServiceId(facultyServiceId);
                log.info("Faculty created in Faculty-Service with ID: {}", facultyServiceId);
                break;

            case LIBRARIAN:
                Long librarianServiceId = roleServiceIntegration.createLibrarianInLibraryService(user);
                user.setLibrarianServiceId(librarianServiceId);
                log.info("Librarian created in Library-Service with ID: {}", librarianServiceId);
                break;

            case ADMIN:
                log.info("Admin user created, no cascade needed");
                break;
        }

        userRepository.save(user);
    }

    /**
     * Get user by ID
     */
    public UserDto getUserById(Long id) {
        log.debug("Fetching user with ID: {}", id);
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        return modelMapper.map(user, UserDto.class);
    }

    /**
     * Get user by email
     */
    public UserDto getUserByEmail(String email) {
        log.debug("Fetching user with email: {}", email);
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return modelMapper.map(user, UserDto.class);
    }

    /**
     * Get user by university ID
     */
    public UserDto getUserByUniversityId(String universityId) {
        log.debug("Fetching user with university ID: {}", universityId);
        UserEntity user = userRepository.findByUniversityId(universityId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with university ID: " + universityId));
        return modelMapper.map(user, UserDto.class);
    }

    /**
     * Get all users by role
     */
    public List<UserDto> getUsersByRole(Role role) {
        log.debug("Fetching all users with role: {}", role);
        List<UserEntity> users = userRepository.findActiveUsersByRole(role);
        return users.stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }

    /**
     * Get all users
     */
    public List<UserDto> getAllUsers() {
        log.debug("Fetching all active users");
        List<UserEntity> users = userRepository.findByActiveTrue();
        return users.stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }

    /**
     * Update user (ADMIN can update any user, others only their own)
     */
    public UserDto updateUser(Long id, UserDto userDto, String updatedBy, Role updaterRole) {
        log.info("Updating user with ID: {} by: {}", id, updatedBy);

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        // Only ADMIN or the user themselves can update
        if (!updaterRole.equals(Role.ADMIN) && !updatedBy.equals(user.getEmail())) {
            log.warn("Unauthorized update attempt by: {} for user: {}", updatedBy, id);
            throw new ForbiddenException("You can only update your own profile");
        }

        // Update allowed fields (not ID or universityId)
        if (userDto.getUsername() != null) user.setUsername(userDto.getUsername());
        if (userDto.getPhoneNumber() != null) user.setPhoneNumber(userDto.getPhoneNumber());
        if (userDto.getDepartment() != null) user.setDepartment(userDto.getDepartment());
        if (userDto.getEmail() != null && !userDto.getEmail().equals(user.getEmail())) {
            // Check if new email exists
            if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
                throw new DuplicateResourceException("Email already exists: " + userDto.getEmail());
            }
            user.setEmail(userDto.getEmail());
        }

        user.setUpdatedBy(updatedBy);
        user.setUpdatedAt(LocalDateTime.now());

        UserEntity updatedUser = userRepository.save(user);
        log.info("User updated successfully with ID: {}", updatedUser.getId());

        return modelMapper.map(updatedUser, UserDto.class);
    }

    /**
     * Delete user (soft delete - cascade to related services)
     */
    public void deleteUser(Long id, String deletedBy) {
        log.info("Deleting user with ID: {} by: {}", id, deletedBy);

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        // Cascade delete from specific services
        cascadeUserDeletion(user);

        user.setActive(false);
        user.setUpdatedBy(deletedBy);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("User deleted successfully with ID: {}", id);
    }

    /**
     * Cascade user deletion to specific services
     */
    private void cascadeUserDeletion(UserEntity user) {
        log.debug("Cascading user deletion for role: {}", user.getRole());

        switch (user.getRole()) {
            case STUDENT:
                if (user.getStudentServiceId() != null) {
                    roleServiceIntegration.deleteStudentFromStudentService(user.getStudentServiceId());
                    log.info("Student deleted from Student-Service with ID: {}", user.getStudentServiceId());
                }
                break;

            case FACULTY:
                if (user.getFacultyServiceId() != null) {
                    roleServiceIntegration.deleteFacultyFromFacultyService(user.getFacultyServiceId());
                    log.info("Faculty deleted from Faculty-Service with ID: {}", user.getFacultyServiceId());
                }
                break;

            case LIBRARIAN:
                if (user.getLibrarianServiceId() != null) {
                    roleServiceIntegration.deleteLibrarianFromLibraryService(user.getLibrarianServiceId());
                    log.info("Librarian deleted from Library-Service with ID: {}", user.getLibrarianServiceId());
                }
                break;

            case ADMIN:
                log.info("Admin user deleted");
                break;
        }
    }

    /**
     * Update password
     */
    public void updatePassword(Long id, String oldPassword, String newPassword, String updatedBy) {
        log.info("Updating password for user ID: {}", id);

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        // Verify old password
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            log.warn("Invalid old password provided for user: {}", id);
            throw new InvalidOperationException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedBy(updatedBy);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("Password updated successfully for user ID: {}", id);
    }

    /**
     * Get user count by role
     */
    public long getUserCountByRole(Role role) {
        log.debug("Getting user count for role: {}", role);
        return userRepository.countByRole(role);
    }

    /**
     * Get user count by role and department
     */
    public long getUserCountByRoleAndDepartment(Role role, String department) {
        log.debug("Getting user count for role: {} and department: {}", role, department);
        return userRepository.countByRoleAndDepartment(role, department);
    }
}