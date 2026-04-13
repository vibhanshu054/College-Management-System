package com.userService.repository;

import com.userService.entity.FacultyEntity;
import com.userService.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacultyRepository extends JpaRepository<FacultyEntity, Long> {}

