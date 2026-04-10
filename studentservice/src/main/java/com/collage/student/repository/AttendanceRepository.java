package com.collage.student.repository;

import com.collage.student.entity.AttendanceRecord;
import com.collage.student.enums.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceRepository extends JpaRepository<AttendanceRecord, Long> {
    Long countByStudentId(Long id);

    long countByStudentIdAndStatus(Long studentId, AttendanceStatus attendanceStatus);

    List<AttendanceRecord> findByStudentIdOrderByAttendanceDateDesc(Long id);
}
