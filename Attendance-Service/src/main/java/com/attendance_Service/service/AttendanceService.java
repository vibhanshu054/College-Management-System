package com.college.attendance_Service.service;


import com.college.attendance_Service.dto.AttendanceDTO;
import com.college.attendance_Service.entity.AttendanceEntity;
import com.college.attendance_Service.enums.AttendanceStatus;
import com.college.attendance_Service.exception.*;
import com.college.attendance_Service.repository.AttendanceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final ModelMapper modelMapper;

    /**
     * Mark attendance for a student
     */
    public AttendanceDTO markAttendance(AttendanceDTO attendanceDTO, String markedBy) {
        log.info("Marking attendance for student {} on {}", attendanceDTO.getStudentId(),
                attendanceDTO.getAttendanceDate());

        AttendanceEntity attendance = modelMapper.map(attendanceDTO, AttendanceEntity.class);
        attendance.setCreatedBy(markedBy);
        attendance.setUpdatedBy(markedBy);
        attendance.setStatus(AttendanceStatus.valueOf(attendanceDTO.getStatus()));

        AttendanceEntity savedAttendance = attendanceRepository.save(attendance);
        log.info("Attendance marked successfully with ID: {}", savedAttendance.getId());

        return modelMapper.map(savedAttendance, AttendanceDTO.class);
    }

    /**
     * Get attendance record by ID
     */
    @Transactional(readOnly = true)
    public AttendanceDTO getAttendanceById(Long id) {
        log.debug("Fetching attendance record with ID: {}", id);
        AttendanceEntity attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found with ID: " + id));
        return modelMapper.map(attendance, AttendanceDTO.class);
    }

    /**
     * Get all attendance records for a student
     */
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getStudentAttendance(String studentId) {
        log.debug("Fetching attendance records for student: {}", studentId);
        return attendanceRepository.findByStudentId(studentId)
                .stream()
                .map(attendance -> modelMapper.map(attendance, AttendanceDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Get attendance for a course on a specific date
     */
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getCourseAttendanceForDate(String courseId, LocalDate date) {
        log.debug("Fetching attendance for course {} on {}", courseId, date);
        return attendanceRepository.findByCourseAndDate(courseId, date)
                .stream()
                .map(attendance -> modelMapper.map(attendance, AttendanceDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Get faculty's marked attendance records
     */
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getFacultyAttendanceRecords(String facultyId, LocalDate startDate, LocalDate endDate) {
        log.debug("Fetching attendance records marked by faculty {} from {} to {}", facultyId, startDate, endDate);
        return attendanceRepository.findByFacultyAndDateRange(facultyId, startDate, endDate)
                .stream()
                .map(attendance -> modelMapper.map(attendance, AttendanceDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Calculate attendance percentage for a student
     */
    @Transactional(readOnly = true)
    public double calculateAttendancePercentage(String studentId, LocalDate startDate, LocalDate endDate) {
        log.debug("Calculating attendance percentage for student: {}", studentId);

        long totalDays = attendanceRepository.countByStudentAndDateRange(studentId, startDate, endDate);
        long presentDays = attendanceRepository.countByStudentAndStatus(studentId, AttendanceStatus.PRESENT);

        if (totalDays == 0) {
            return 0.0;
        }

        return (double) presentDays / totalDays * 100;
    }

    /**
     * Update attendance record
     */
    public AttendanceDTO updateAttendance(Long id, AttendanceDTO attendanceDTO, String updatedBy) {
        log.info("Updating attendance record with ID: {}", id);

        AttendanceEntity attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found with ID: " + id));

        if (attendanceDTO.getStatus() != null) {
            attendance.setStatus(AttendanceStatus.valueOf(attendanceDTO.getStatus()));
        }
        if (attendanceDTO.getRemarks() != null) {
            attendance.setRemarks(attendanceDTO.getRemarks());
        }

        attendance.setUpdatedBy(updatedBy);
        attendance.setUpdatedAt(LocalDateTime.now());

        AttendanceEntity updatedAttendance = attendanceRepository.save(attendance);
        log.info("Attendance record updated successfully with ID: {}", updatedAttendance.getId());

        return modelMapper.map(updatedAttendance, AttendanceDTO.class);
    }

    /**
     * Delete attendance record (soft delete)
     */
    public void deleteAttendance(Long id, String deletedBy) {
        log.info("Deleting attendance record with ID: {}", id);

        attendanceRepository.deleteById(id);
        log.info("Attendance record deleted with ID: {}", id);
    }
}