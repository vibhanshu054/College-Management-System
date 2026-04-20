package com.student.repository;

import com.student.dto.CourseStudentCountProjection;
import com.student.entity.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<StudentEntity, Long> {
    @Query("""
           SELECT s.course as course, COUNT(s) as totalStudents
           FROM StudentEntity s
           WHERE s.course IS NOT NULL AND s.course <> ''
           GROUP BY s.course
           ORDER BY s.course
           """)
    List<CourseStudentCountProjection> getStudentCountByCourse();
    Optional<StudentEntity> findByUniversityId(String universityId);

    Optional<StudentEntity> findByStudentEmail(String studentEmail);

    List<StudentEntity> findByFacultyUniversityId(String facultyUniversityId);

    List<StudentEntity> findByActiveTrue();

    List<StudentEntity> findByCourseCodeAndActiveTrue(String courseCode);

    List<StudentEntity> findBySemesterAndActiveTrue(String semester);

    List<StudentEntity> findByDepartmentAndActiveTrue(String department);

    List<StudentEntity> findByCourseCodeAndSemesterAndActiveTrue(String courseCode, String semester);

    List<StudentEntity> findByCourseCodeAndDepartmentAndActiveTrue(String courseCode, String department);

    List<StudentEntity> findBySemesterAndDepartmentAndActiveTrue(String semester, String department);

    List<StudentEntity> findByCourseCodeAndSemesterAndDepartmentAndActiveTrue(
            String courseCode,
            String semester,
            String department
    );

    long countByActiveTrue();

    long countByCourseCodeAndActiveTrue(String courseCode);

    long countByDepartmentAndActiveTrue(String department);
}