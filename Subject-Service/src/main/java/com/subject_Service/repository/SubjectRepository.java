package com.subject_Service.repository;




import com.subject_Service.entity.SubjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<SubjectEntity, Long> {

    Optional<SubjectEntity> findBySubjectCode(String code);

    @Query("SELECT s FROM SubjectEntity s WHERE s.courseId = ?1 AND s.active = true")
    List<SubjectEntity> findByCourseId(String courseId);

    @Query("SELECT s FROM SubjectEntity s WHERE s.departmentId = ?1 AND s.active = true")
    List<SubjectEntity> findByDepartmentId(String departmentId);

    @Query("SELECT s FROM SubjectEntity s WHERE s.semester = ?1 AND s.active = true")
    List<SubjectEntity> findBySemester(Integer semester);

    @Query("SELECT s FROM SubjectEntity s WHERE s.facultyId = ?1 AND s.active = true")
    List<SubjectEntity> findByFacultyId(String facultyId);

    @Query("SELECT s FROM SubjectEntity s WHERE s.active = true")
    List<SubjectEntity> findAllActive();

    @Query("SELECT COUNT(s) FROM SubjectEntity s WHERE s.courseId = ?1")
    long countByCourseId(String courseId);
}