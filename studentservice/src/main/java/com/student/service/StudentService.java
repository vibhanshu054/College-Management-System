package com.student.service;

import com.student.dto.StudentDTO;
import com.student.enums.AttendanceStatus;

import java.util.List;
import java.util.Map;

public interface StudentService {

    Long createStudent(StudentDTO studentDto);

    StudentDTO getStudent(Long id);

    StudentDTO getStudentByUniversityId(String universityId);

    List<StudentDTO> getAllStudents(String courseCode, String semester, String department);

    StudentDTO updateStudent(Long id, StudentDTO studentDto);

    void deleteStudent(Long id);

    Map<String, Object> getStudentDashboard(Long id);

    void markAttendance(Long studentId, AttendanceStatus status, Long facultyId, String courseCode);

    void recalculateAttendancePercentage(Long studentId);

    List<Map<String, Object>> getAttendanceCalendar(Long id);

    void incrementBooksIssued(Long studentId);

    void incrementBooksReturned(Long studentId);

    List<StudentDTO> getStudentsByCourse(String courseCode);

    long getTotalStudentsCount();

    long getStudentsCountByCourse(String courseCode);

    List<StudentDTO> getStudentsByDepartment(String department);

    long getStudentsCountByDepartment(String department);

    Map<String, Integer> getBooksStatus(Long id);
}