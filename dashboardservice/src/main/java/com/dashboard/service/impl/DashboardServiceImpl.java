package com.dashboard.service.impl;

import com.dashboard.clients.*;
import com.dashboard.dto.*;
import com.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardServiceImpl implements DashboardService {

    private final UserServiceClient userServiceClient;
    private final StudentServiceClient studentServiceClient;
    private final FacultyServiceClient facultyServiceClient;
    private final CourseServiceClient courseServiceClient;
    private final DepartmentServiceClient departmentServiceClient;
    private final LibraryServiceClient libraryServiceClient;
    private final AttendanceServiceClient attendanceServiceClient;
    private final SubjectServiceClient subjectServiceClient;

    /**
     * ADMIN DASHBOARD
     */
    public AdminDashboardDTO getAdminDashboard() {

        long totalStudents = studentServiceClient.getTotalStudentsCount()
                .getOrDefault("totalStudents", 0L);

        long totalFaculty = facultyServiceClient.getTotalFacultyCount()
                .getOrDefault("totalFaculty", 0L);

        long totalDepartments = departmentServiceClient.getTotalDepartmentsCount()
                .getOrDefault("totalDepartments", 0L);

        long totalCourses = courseServiceClient.getTotalCoursesCount()
                .getOrDefault("totalCourses", 0L);

        Map<String, Long> userDistribution = new HashMap<>();
        userDistribution.put("students", totalStudents);
        userDistribution.put("faculty", totalFaculty);

        return AdminDashboardDTO.builder()
                .totalStudents(totalStudents)
                .totalFaculty(totalFaculty)
                .totalDepartments(totalDepartments)
                .totalCourses(totalCourses)
                .userDistribution(userDistribution)
                .build();
    }

    /**
     * STUDENT DASHBOARD
     */
    @Override
    public StudentDashboardDTO getStudentDashboard(String studentId) {

        //  1. Call Attendance Service
        List<?> attendanceList = attendanceServiceClient.getStudentAttendance(studentId);

        double percentage = calculateAttendance(attendanceList);

        //  2. Call Subject Service
        List<SubjectDTO> subjects = subjectServiceClient.getSubjectsByStudent(studentId);

        //  3. Total classes (simple logic)
        int totalClasses = (attendanceList != null) ? attendanceList.size() : 0;

        //  4. Build response
        return StudentDashboardDTO.builder()
                .studentName(studentId)
                .courseName("N/A")   // can improve later
                .attendancePercentage(percentage)
                .booksIssued(0)
                .booksReturned(0)
                .totalClasses(totalClasses)
                .enrolledSubjects(subjects)
                .build();
    }

    /**
     * FACULTY DASHBOARD
     */
    @Override
    public FacultyDashboardDTO getFacultyDashboard(String facultyId) {

        // 1. Get all subjects ONCE
        List<SubjectDTO> subjects = subjectServiceClient.getAllSubjects()
                .stream()
                .filter(sub -> facultyId.equals(sub.getFacultyId()))
                .toList();

        int totalCourses = subjects.size();

        // 2. Total students (simple for now)
        int totalStudents = studentServiceClient.getAllStudents().size();

        // 3. Attendance (TEMP FIX - faculty attendance not valid)
        double percentage = 0.0;

        // 4. Today's schedule (DYNAMIC)
        DayOfWeek today = LocalDate.now().getDayOfWeek();

        List<Map<String, Object>> schedule = subjects.stream()
                .filter(sub -> sub.getDay() != null &&
                        sub.getDay().equalsIgnoreCase(today.name()))
                .map(sub -> {

                    if (sub.getScheduleTime() == null) return null;

                    Map<String, Object> map = new HashMap<>();
                    map.put("course", sub.getSubjectName());

                    try {
                        String[] time = sub.getScheduleTime().split("-");
                        map.put("start", time[0]);
                        map.put("end", time[1]);
                    } catch (Exception e) {
                        log.error("Invalid schedule format for subject: {}", sub.getSubjectName());
                    }

                    return map;
                })
                .filter(obj -> obj != null)
                .toList();

        return FacultyDashboardDTO.builder()
                .facultyName(facultyId)
                .totalClassesAssigned(totalCourses)
                .totalStudents(totalStudents)
                .attendancePercentage(percentage)
                .todaySchedule(schedule)
                .build();
    }
    /**
     * LIBRARIAN DASHBOARD
     */
    public LibrarianDashboardDTO getLibrarianDashboard() {

        Map<String, Object> data = libraryServiceClient.getLibraryDashboard();

        return LibrarianDashboardDTO.builder()
                .totalBooks((Integer) data.getOrDefault("totalBooks", 0))
                .availableBooks((Integer) data.getOrDefault("availableBooks", 0))
                .issuedBooks((Integer) data.getOrDefault("issuedBooks", 0))
                .build();
    }

    /**
     * ROLE BASED DASHBOARD
     */
    public DashboardDTO getDashboardByRole(String role, String userId) {

        DashboardDTO dashboard = new DashboardDTO();
        dashboard.setLastUpdated(LocalDateTime.now());

        switch (role.toUpperCase()) {

            case "ADMIN":
                dashboard.setAdminDashboard(getAdminDashboard());
                break;

            case "STUDENT":
                dashboard.setStudentDashboard(getStudentDashboard(userId));
                break;

            case "FACULTY":
                dashboard.setFacultyDashboard(getFacultyDashboard(userId));
                break;

            case "LIBRARIAN":
                dashboard.setLibrarianDashboard(getLibrarianDashboard());
                break;
        }

        return dashboard;
    }

    /**
     * SIMPLE ATTENDANCE CALCULATION
     */
    public double calculateAttendance(List<?> list) {

        if (list == null || list.isEmpty()) return 0.0;

        long present = list.stream()
                .filter(obj -> obj.toString().contains("PRESENT"))
                .count();

        return (double) present / list.size() * 100;
    }

}