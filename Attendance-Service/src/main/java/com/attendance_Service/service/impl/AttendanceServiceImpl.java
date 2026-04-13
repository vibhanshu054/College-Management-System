package com.attendance_Service.service.impl;

import com.attendance_Service.dto.AttendanceDTO;
import com.attendance_Service.entity.AttendanceEntity;
import com.attendance_Service.enums.AttendanceStatus;
import com.attendance_Service.exception.ResourceNotFoundException;
import com.attendance_Service.repository.AttendanceRepository;
import com.attendance_Service.service.AttendanceService;
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
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final ModelMapper modelMapper;

    @Override
    public AttendanceDTO markAttendance(AttendanceDTO attendanceDTO, String markedBy) {

        String user = (markedBy != null) ? markedBy : "SYSTEM";

        AttendanceEntity attendance = modelMapper.map(attendanceDTO, AttendanceEntity.class);

        attendance.setCreatedBy(user);
        attendance.setUpdatedBy(user);

        attendance.setStatus(
                AttendanceStatus.valueOf(attendanceDTO.getStatus().toUpperCase())
        );

        AttendanceEntity saved = attendanceRepository.save(attendance);

        return modelMapper.map(saved, AttendanceDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceDTO getAttendanceById(Long id) {
        AttendanceEntity attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found: " + id));

        return modelMapper.map(attendance, AttendanceDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getStudentAttendance(String studentId) {
        return attendanceRepository.findByStudentId(studentId)
                .stream()
                .map(a -> modelMapper.map(a, AttendanceDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getCourseAttendanceForDate(String courseId, LocalDate date) {
        return attendanceRepository.findByCourseAndDate(courseId, date)
                .stream()
                .map(a -> modelMapper.map(a, AttendanceDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getFacultyAttendanceRecords(String facultyId, LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.findByFacultyAndDateRange(facultyId, startDate, endDate)
                .stream()
                .map(a -> modelMapper.map(a, AttendanceDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public double calculateAttendancePercentage(String studentId, LocalDate startDate, LocalDate endDate) {

        long totalDays = attendanceRepository
                .countByStudentAndDateRange(studentId, startDate, endDate);

        long presentDays = attendanceRepository
                .countByStudentAndStatusAndDateRange(studentId, AttendanceStatus.PRESENT, startDate, endDate);

        if (totalDays == 0) return 0.0;

        return (double) presentDays / totalDays * 100;
    }

    @Override
    public AttendanceDTO updateAttendance(Long id, AttendanceDTO dto, String updatedBy) {

        String user = (updatedBy != null) ? updatedBy : "SYSTEM";

        AttendanceEntity attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found: " + id));

        if (dto.getStatus() != null) {
            attendance.setStatus(
                    AttendanceStatus.valueOf(dto.getStatus().toUpperCase())
            );
        }

        if (dto.getRemarks() != null) {
            attendance.setRemarks(dto.getRemarks());
        }

        attendance.setUpdatedBy(user);
        attendance.setUpdatedAt(LocalDateTime.now());

        AttendanceEntity updated = attendanceRepository.save(attendance);

        return modelMapper.map(updated, AttendanceDTO.class);
    }

    @Override
    public void deleteAttendance(Long id, String deletedBy) {
        attendanceRepository.deleteById(id);
    }
}