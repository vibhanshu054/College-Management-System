package com.facultyService.repository;

import com.facultyService.entity.FacultyEntity;
import com.facultyService.enums.FacultySubRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface FacultyRepository extends JpaRepository<FacultyEntity, Long> {

    Optional<FacultyEntity> findByFacultyUniversityId(String facultyUniversityId);

    List<FacultyEntity> findByDepartmentIgnoreCase(String department);

    List<FacultyEntity> findBySubRole(FacultySubRole subRole);

    List<FacultyEntity> findByDepartmentIgnoreCaseAndSubRole(String department, FacultySubRole subRole);

    Integer countByActiveTrue();

    Optional<FacultyEntity> findByFacultyEmail(String facultyEmail);

    Optional<FacultyEntity> findTopByDepartmentOrderByIdDesc(String department);
}