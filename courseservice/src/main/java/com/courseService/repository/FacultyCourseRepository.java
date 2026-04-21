package com.courseService.repository;

import com.courseService.entity.FacultyCourseMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FacultyCourseRepository extends JpaRepository<FacultyCourseMapping, Long> {

    List<FacultyCourseMapping> findByFacultyUniversityId(String facultyUniversityId);

    boolean existsByFacultyUniversityIdAndCourseCode(String facultyUniversityId, String courseCode);
}