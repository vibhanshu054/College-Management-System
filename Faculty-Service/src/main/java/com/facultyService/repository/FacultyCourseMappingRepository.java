package com.facultyService.repository;

import com.facultyService.entity.FacultyCourseMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FacultyCourseMappingRepository extends JpaRepository<FacultyCourseMapping, Long> {

    List<FacultyCourseMapping> findByFacultyUniversityId(String facultyUniversityId);

    boolean existsByFacultyUniversityIdAndCourseCode(String facultyUniversityId, String courseCode);
}