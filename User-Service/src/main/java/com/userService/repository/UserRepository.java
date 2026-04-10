package com.userService.repository;


import com.userService.entity.UserEntity;
import com.userService.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<com.userService.entity.UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByUniversityId(String universityId);

    List<UserEntity> findByRole(Role role);

    List<UserEntity> findByDepartment(String department);

    List<UserEntity> findByRoleAndDepartment(Role role, String department);

    List<UserEntity> findByActiveTrue();

    @Query("SELECT u FROM UserEntity u WHERE u.role = ?1 AND u.active = true")
    List<UserEntity> findActiveUsersByRole(Role role);

    @Query("SELECT COUNT(u) FROM UserEntity u WHERE u.role = ?1")
    long countByRole(Role role);

    @Query("SELECT COUNT(u) FROM UserEntity u WHERE u.role = ?1 AND u.department = ?2")
    long countByRoleAndDepartment(Role role, String department);
}