package com.userService.services;

import com.userService.dto.UpdatePasswordDto;
import com.userService.dto.UserDto;
import com.userService.entity.UserEntity;


import java.util.List;


public interface UserService {
    List<UserEntity> getAllUser();

    UserDto findByUsername(String username);

    UserDto save(UserDto dto);

    UserDto getUser(Long userId);

    UserDto login(String username, String password);

    String updatePassword(UpdatePasswordDto dto);

    UserDto findByEmail(String email);
}
