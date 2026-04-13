package com.userService.repository;

import com.userService.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<UserEntity, Long> {}

