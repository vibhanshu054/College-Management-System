package com.facultyService.service;

import com.facultyService.dto.ApiResponse;
import com.facultyService.dto.FacultyDTO;
import com.student.enums.AttendanceStatus;

import java.util.List;
import java.util.Map;

public interface FacultyService {

    // ================= PROFILE =================
    ApiResponse createFaculty(FacultyDTO facultyDTO);

    ApiResponse getFaculty(String universityId);

    ApiResponse getAllFaculty(String department, String subRole);

    ApiResponse updateFaculty(String universityId, FacultyDTO facultyDTO);

    ApiResponse deleteFaculty(String universityId);


    // ================= DASHBOARD =================
    ApiResponse getDashboard(String universityId);

    ApiResponse getFacultyDashboard(String universityId);


    // ================= SCHEDULE =================
    ApiResponse getSchedule(String universityId);

    ApiResponse getAttendanceCalendar(String universityId);

    ApiResponse updateSchedule(String universityId, Map<String, Object> scheduleData);

    ApiResponse assignSchedule(String facultyId, Map<String, Object> schedule);

    ApiResponse assignScheduleById(String universityId, Map<String, Object> schedule);


    // ================= COURSES =================
    ApiResponse getAssignedCourses(String facultyId);

    ApiResponse getCoursesById(String universityId);

    ApiResponse assignCourses(String facultyId, List<Long> courseIds);

    ApiResponse assignCoursesById(String universityId, List<Long> courseIds);


    // ================= STUDENTS =================
    ApiResponse getStudents(String facultyId);

    ApiResponse getStudentsById(String universityId);

    ApiResponse getTotalStudents(String facultyId);

    ApiResponse getStudentCountById(String universityId);


    // ================= ATTENDANCE =================
    ApiResponse getAttendance(String facultyId);

    ApiResponse getAttendanceById(String universityId);
    ApiResponse markFacultySelfAttendance(String universityId,Long facultyId);
    ApiResponse markAttendance(String universityId,
                               AttendanceStatus status,
                               Long facultyId,
                               String courseCode);
    // ================= BOOKS =================
    ApiResponse updateBookStatsByFacultyUniversityId(String facultyUniversityId, int issued, int returned);

    ApiResponse getFacultyByFacultyUniversityId(String facultyUniversityId);
}