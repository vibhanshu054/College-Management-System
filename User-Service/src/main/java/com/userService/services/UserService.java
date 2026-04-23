package com.userService.services;

import com.userService.dto.UserDto;
import com.userService.enums.UserRole;

import java.util.List;


public interface UserService {

    UserDto createUser(UserDto dto, String createdBy, String role);

    UserDto getUserById(Long id);

    List<UserDto> getUsersByRole(String role);

    List<UserDto> getAllUsers(String role);

    UserDto updateUser(Long id, UserDto userDto, String updatedBy, UserRole role);

    void deleteUser(Long id, String deletedBy, String role);

    void updatePassword(Long id, String oldPassword, String newPassword, String updatedBy);

    long getUserCountByRole(String role);

    long getUserCountByRoleAndDepartment(String role, String department);

    UserDto getUserByEmail(String email);
    void resetPasswordByUsername(String username, String newPassword);

    UserDto verifyCredentials(String email, String rawPassword);

    UserDto getUserByUniversityId(String universityId);

    void deleteUserByUniversityId(String universityId, String username, String role);

    UserDto updateUserByUniversityId(String universityId, UserDto dto, String username);

    void updatePasswordByUniversityId(String universityId, String oldPassword, String newPassword, String username);

    void resetPasswordByEmail(String email, String newPassword);
}