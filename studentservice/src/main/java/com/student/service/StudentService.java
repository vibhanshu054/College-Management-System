package com.student.service;

import com.student.dto.ApiResponse;
import com.student.enums.AttendanceStatus;

import java.util.List;

public interface StudentService {

    ApiResponse getStudentsByFacultyUniversityId(String facultyUniversityId);

    ApiResponse createStudent(StudentCreateRequest request, String createdBy, String role);

    ApiResponse getStudent(Long id);

    ApiResponse getStudentByUniversityId(String universityId);

    ApiResponse getAllStudents(String courseCode, String semester, String department, String role);

    ApiResponse updateStudent(String universityId, StudentUpdateRequest request, String updatedBy, String role);

    ApiResponse deleteStudent(String universityId, String deletedBy, String role);

    ApiResponse getStudentDashboard(String universityId);

    ApiResponse markAttendance(String universityId, AttendanceStatus status, Long facultyId, String courseCode);

    void recalculateAttendancePercentage(String universityId);

    ApiResponse getAttendanceCalendar(String universityId);

    void incrementBooksIssued(String universityId);

    void incrementBooksReturned(String universityId);

    ApiResponse getStudentsByCourse(String courseCode);

    ApiResponse getTotalStudentsCount();

    ApiResponse getStudentsCountByCourse(String courseCode);

    ApiResponse getStudentsByDepartment(String department);

    ApiResponse getStudentsCountByDepartment(String department);

    ApiResponse getBooksStatus(String universityId);

    ApiResponse updateBookStats(String universityId, int issued, int returned);

    ApiResponse updateSubjects(String universityId, List<String> subjects, String role);

    ApiResponse getStudentCountByCourse();

    ApiResponse updateSemester(String universityId, String semester, String role);
}