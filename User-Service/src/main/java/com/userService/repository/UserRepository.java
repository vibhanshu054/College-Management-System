package com.userService.repository;


import com.userService.entity.UserEntity;
import com.userService.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByUniversityId(String universityId);

    Optional<UserEntity> findByUsername(String username);

    @Query("SELECT u FROM UserEntity u WHERE u.role = ?1 AND u.active = true")
    List<UserEntity> findByRoleAndActiveTrue(UserRole role);

    @Query("SELECT u FROM UserEntity u WHERE u.active = true")
    List<UserEntity> findByActiveTrue();

    @Query("SELECT COUNT(u) FROM UserEntity u WHERE u.role = ?1 AND u.active = true")
    long countByRoleAndActiveTrue(UserRole role);

    @Query("SELECT COUNT(u) FROM UserEntity u WHERE u.role = ?1 AND u.department = ?2 AND u.active = true")
    long countByRoleAndDepartmentAndActiveTrue(UserRole role, String department);
}