package com.student.service;

import com.student.dto.ApiResponse;
import com.student.dto.StudentDTO;
import com.student.dto.UserDto;
import com.student.enums.AttendanceStatus;

import java.util.List;

public interface StudentService {



    ApiResponse updateSemester(String universityId, String semester, String role);

    ApiResponse getStudentCountByCourse();

    ApiResponse updateSubjects(String universityId, List<String> subjects, String role);

    ApiResponse updateBookStats(String universityId, int issued, int returned);

    ApiResponse getStudentsByFacultyUniversityId(String facultyUniversityId);

    ApiResponse createStudent(StudentDTO dto, String createdBy, String role);

    ApiResponse getStudent(Long id);

    ApiResponse getStudentByUniversityId(String universityId);

    ApiResponse getAllStudents(String courseCode, String semester, String department, String role);

    ApiResponse updateStudent(String universityId, StudentDTO studentDto, String updatedBy, String role);

    ApiResponse deleteStudent(String universityId, String deletedBy, String role);

    ApiResponse getStudentDashboard(String universityId);

    void recalculateAttendancePercentage(String universityId);

    ApiResponse getAttendanceCalendar(String universityId);

    ApiResponse markAttendance(String universityId, AttendanceStatus status, Long facultyId, String courseCode);

    ApiResponse getStudentsCountByDepartment(String department);

    ApiResponse getBooksStatus(String universityId);

    void incrementBooksIssued(String universityId);

    void incrementBooksReturned(String universityId);

    ApiResponse getStudentsByCourse(String courseCode);

    ApiResponse getStudentsCountByCourse(String courseCode);

    ApiResponse getStudentsByDepartment(String department);

    ApiResponse getTotalStudentsCount();
}