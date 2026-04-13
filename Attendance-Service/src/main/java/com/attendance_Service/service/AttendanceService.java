package com.attendance_Service.service;

import com.attendance_Service.dto.AttendanceDTO;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {

    AttendanceDTO markAttendance(AttendanceDTO dto, String user);

    AttendanceDTO getAttendanceById(Long id);

    List<AttendanceDTO> getStudentAttendance(String studentId);

    List<AttendanceDTO> getCourseAttendanceForDate(String courseId, LocalDate date);

    List<AttendanceDTO> getFacultyAttendanceRecords(String facultyId, LocalDate startDate, LocalDate endDate);

    double calculateAttendancePercentage(String studentId, LocalDate startDate, LocalDate endDate);

    AttendanceDTO updateAttendance(Long id, AttendanceDTO dto, String user);

    void deleteAttendance(Long id, String user);
}