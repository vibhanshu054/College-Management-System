package com.attendance_Service.repository;





import com.attendance_Service.entity.AttendanceEntity;
import com.attendance_Service.enums.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceEntity, Long> {

    @Query("SELECT a FROM AttendanceEntity a WHERE a.studentId = ?1 ORDER BY a.attendanceDate DESC")
    List<AttendanceEntity> findByStudentId(String studentId);

    @Query("SELECT a FROM AttendanceEntity a WHERE a.courseId = ?1 AND a.attendanceDate = ?2")
    List<AttendanceEntity> findByCourseAndDate(String courseId, LocalDate date);

    @Query("SELECT a FROM AttendanceEntity a WHERE a.facultyId = ?1 AND a.attendanceDate BETWEEN ?2 AND ?3")
    List<AttendanceEntity> findByFacultyAndDateRange(String facultyId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT COUNT(a) FROM AttendanceEntity a WHERE a.studentId = ?1 AND a.status = ?2")
    long countByStudentAndStatus(String studentId, AttendanceStatus status);

    @Query("SELECT COUNT(a) FROM AttendanceEntity a WHERE a.studentId = ?1 AND a.attendanceDate BETWEEN ?2 AND ?3")
    long countByStudentAndDateRange(String studentId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT COUNT(a) FROM AttendanceEntity a WHERE a.studentId = ?1 AND a.status = ?2 AND a.attendanceDate BETWEEN ?3 AND ?4")
    long countByStudentAndStatusAndDateRange(String studentId, AttendanceStatus status, LocalDate startDate, LocalDate endDate);
}