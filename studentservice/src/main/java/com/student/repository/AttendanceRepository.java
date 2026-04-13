package com.student.repository;

import com.student.entity.AttendanceRecord;
import com.student.enums.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceRepository extends JpaRepository<AttendanceRecord, Long> {

    long countByStudentId(Long studentId);

    long countByStudentIdAndStatus(Long studentId, AttendanceStatus attendanceStatus);

    List<AttendanceRecord> findByStudentIdOrderByAttendanceDateDesc(Long studentId);
}