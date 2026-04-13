package com.facultyService.repository;

import com.facultyService.entity.FacultyAttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface FacultyAttendanceRepository extends JpaRepository<FacultyAttendanceRecord, Long> {
    List<FacultyAttendanceRecord> findByFacultyId(Long facultyId);
    List<FacultyAttendanceRecord> findByFacultyIdAndAttendanceDateBetween(Long facultyId, LocalDate start, LocalDate end);
}