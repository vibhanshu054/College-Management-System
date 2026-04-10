package com.userService.services;

import com.userService.dto.UpdatePasswordDto;
import com.userService.dto.UserDto;
import com.userService.entity.UserEntity;
import com.userService.enums.Role;


import java.util.List;


public interface UserService {
    List<UserEntity> getAllUser();

    UserDto findByUsername(String username);

    UserDto save(UserDto dto);

    UserDto getUser(Long userId);

    UserDto login(String username, String password);

    String updatePassword(UpdatePasswordDto dto);

    UserDto findByEmail(String email);

    UserDto createUser(UserDto userDTO, String name);

    UserDto getUserById(Long id);

    UserDto updateUser(Long id, UserDto userDTO, String name, Role role);

    void updatePassword(Long id, String oldPassword, String newPassword, String name);

    long getUserCountByRoleAndDepartment(Role role, String department);

    long getUserCountByRole(Role role);

    void deleteUser(Long id, String name);

    UserDto getUserByUniversityId(String universityId);

    UserDto getUserByEmail(String email);

    List<UserDto> getUsersByRole(Role role);

    List<UserDto> getAllUsers();
}
