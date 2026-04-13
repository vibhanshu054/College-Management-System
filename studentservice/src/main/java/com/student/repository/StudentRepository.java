package com.student.repository;

import com.student.entity.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<StudentEntity, Long> {

    Optional<StudentEntity> findByUniversityId(String universityId);

    Optional<StudentEntity> findByStudentEmail(String studentEmail);

    List<StudentEntity> findByCourseCode(String courseCode);

    List<StudentEntity> findByDepartment(String department);

    List<StudentEntity> findByCourseAndSemester(String course, String semester);

    List<StudentEntity> findByActiveTrue();

    @Query("SELECT s FROM StudentEntity s WHERE s.courseCode = ?1 AND s.semester = ?2 AND s.department = ?3")
    List<StudentEntity> findByFilters(String courseCode, String semester, String department);

    @Query("SELECT COUNT(s) FROM StudentEntity s WHERE s.courseCode = ?1")
    long countByCourseCode(String courseCode);

    @Query("SELECT COUNT(s) FROM StudentEntity s WHERE s.department = ?1")
    long countByDepartment(String department);
}