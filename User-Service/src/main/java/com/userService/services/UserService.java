package com.userService.services;

import com.userService.dto.UserDto;
import com.userService.enums.UserRole;

import java.util.List;


public interface UserService {

    UserDto createUser(UserDto userDto, String createdBy);

    UserDto getUserById(Long id);

    List<UserDto> getUsersByRole(String role);

    List<UserDto> getAllUsers();

    UserDto updateUser(Long id, UserDto userDto, String updatedBy, UserRole userRole);

    void deleteUser(Long id, String deletedBy);

    void updatePassword(Long id, String oldPassword, String newPassword, String updatedBy);

    long getUserCountByRole(String role);

    long getUserCountByRoleAndDepartment(String role, String department);

    UserDto getUserByEmail(String email);


    UserDto verifyCredentials(String email, String rawPassword);

    UserDto getUserByUniversityId(String universityId);
}