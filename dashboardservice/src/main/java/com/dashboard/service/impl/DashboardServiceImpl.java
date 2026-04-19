package com.dashboard.service.impl;

import com.dashboard.clients.*;
import com.dashboard.dto.ApiResponse;
import com.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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

    @Override
    public ApiResponse getAdminDashboard() {
        try {
            long totalStudents = extractCount(
                    studentServiceClient.getTotalStudentsCount().getBody().getData(),
                    "totalStudents"
            );

            long totalCourses = extractCount(
                    courseServiceClient.getTotalCoursesCount().getBody().getData(),
                    "totalCourses"
            );

            long totalDepartments = extractCount(
                    departmentServiceClient.getTotalDepartmentsCount().getBody().getData(),
                    "totalDepartments"
            );

            Object facultyData = facultyServiceClient.getAllFaculty().getBody().getData();
            long totalFaculty = (facultyData instanceof List<?>) ? ((List<?>) facultyData).size() : 0;

            Map<String, Object> data = new HashMap<>();
            data.put("totalStudents", totalStudents);
            data.put("totalCourses", totalCourses);
            data.put("totalDepartments", totalDepartments);
            data.put("totalFaculty", totalFaculty);

            return new ApiResponse("Admin dashboard", 200, data, LocalDateTime.now());
        } catch (Exception e) {
            log.error("Admin dashboard error", e);
            throw new RuntimeException("Admin dashboard failed");
        }
    }

    @Override
    public ApiResponse getFacultyDashboard(String facultyUniversityId) {
        validateId(facultyUniversityId, "Faculty");

        try {
            List<Map<String, Object>> courses = fetchFacultyCourses(facultyUniversityId);
            int totalCourses = courses.size();

            int totalStudents = fetchTotalStudentsSafely(facultyUniversityId);
            int totalSchedules = fetchTotalSchedulesSafely(facultyUniversityId);
            List<Map<String, Object>> todaySchedule = fetchTodayScheduleSafely(facultyUniversityId);

            Map<String, Object> result = new HashMap<>();
            result.put("facultyUniversityId", facultyUniversityId);
            result.put("courses", courses);
            result.put("totalCourses", totalCourses);
            result.put("totalStudents", totalStudents);
            result.put("totalSchedules", totalSchedules);
            result.put("todaySchedule", todaySchedule);

            return new ApiResponse("Faculty dashboard", 200, result, LocalDateTime.now());

        } catch (Exception e) {
            log.error("Faculty dashboard error for facultyUniversityId={}", facultyUniversityId, e);
            throw new RuntimeException("Faculty dashboard failed");
        }
    }

    @Override
    public ApiResponse getStudentDashboard(String universityId) {
        validateId(universityId, "Student");

        try {
            List<?> attendanceList = attendanceServiceClient.getStudentAttendance(universityId);
            double attendance = calculateAttendance(attendanceList);

            Object subjectData = subjectServiceClient
                    .getSubjectsByStudentUniversityId(universityId)
                    .getBody()
                    .getData();

            List<?> subjects = (subjectData instanceof List<?>) ? (List<?>) subjectData : Collections.emptyList();

            Object libraryRaw = libraryServiceClient
                    .getBookCountByUser(universityId)
                    .getBody()
                    .getData();

            Map<String, Object> libraryData =
                    (libraryRaw instanceof Map<?, ?> map) ? (Map<String, Object>) map : new HashMap<>();

            Map<String, Object> data = new HashMap<>();
            data.put("studentId", universityId);
            data.put("attendancePercentage", attendance);
            data.put("totalClasses", attendanceList != null ? attendanceList.size() : 0);
            data.put("subjects", subjects);
            data.put("booksIssued", libraryData.getOrDefault("issued", 0));
            data.put("booksReturned", libraryData.getOrDefault("returned", 0));

            return new ApiResponse("Student dashboard", 200, data, LocalDateTime.now());

        } catch (Exception e) {
            log.error("Student dashboard error", e);
            throw new RuntimeException("Student dashboard failed");
        }
    }

    @Override
    public ApiResponse getLibrarianDashboard() {
        try {
            return libraryServiceClient.getLibraryDashboard().getBody();
        } catch (Exception e) {
            log.error("Library error", e);
            throw new RuntimeException("Library dashboard failed");
        }
    }

    @Override
    public ApiResponse getDashboardByRole(String role, String userId) {
        validateRole(role);

        return switch (role.toUpperCase()) {
            case "ADMIN" -> getAdminDashboard();
            case "STUDENT" -> getStudentDashboard(userId);
            case "FACULTY" -> getFacultyDashboard(userId);
            case "LIBRARIAN" -> getLibrarianDashboard();
            default -> throw new IllegalArgumentException("Invalid role");
        };
    }

    @Override
    public double calculateAttendance(List<?> list) {
        if (list == null || list.isEmpty()) return 0;

        long present = list.stream()
                .filter(a -> a != null && a.toString().toUpperCase().contains("P"))
                .count();

        return Math.round((present * 100.0 / list.size()) * 100.0) / 100.0;
    }

    // ================= PRIVATE HELPERS =================

    private List<Map<String, Object>> fetchFacultyCourses(String facultyUniversityId) {
        try {
            ResponseEntity<ApiResponse> response = courseServiceClient.getCoursesByFaculty(facultyUniversityId);

            if (response == null || response.getBody() == null || response.getBody().getData() == null) {
                return Collections.emptyList();
            }

            Object data = response.getBody().getData();

            if (!(data instanceof List<?> rawList)) {
                return Collections.emptyList();
            }

            List<Map<String, Object>> result = new ArrayList<>();

            for (Object obj : rawList) {
                if (obj instanceof Map<?, ?> rawMap) {
                    Map<String, Object> course = new HashMap<>();
                    course.put("courseId", rawMap.get("courseId"));
                    course.put("courseName", rawMap.get("courseName"));
                    course.put("courseCode", rawMap.get("courseCode"));
                    result.add(course);
                }
            }

            return result;

        } catch (Exception e) {
            log.warn("Failed to fetch faculty courses for {}", facultyUniversityId, e);
            return Collections.emptyList();
        }
    }

    private int fetchTotalStudentsSafely(String facultyUniversityId) {
        try {
            ResponseEntity<ApiResponse> response = facultyServiceClient.getTotalStudents(facultyUniversityId);

            if (response == null || response.getBody() == null || response.getBody().getData() == null) {
                return 0;
            }

            return extractInt(response.getBody().getData(), "count");
        } catch (Exception e) {
            log.warn("Student count fetch failed for {} -> fallback 0", facultyUniversityId, e);
            return 0;
        }
    }

    private int fetchTotalSchedulesSafely(String facultyUniversityId) {
        try {
            ResponseEntity<ApiResponse> response = facultyServiceClient.getSchedule(facultyUniversityId);

            if (response == null || response.getBody() == null || response.getBody().getData() == null) {
                return 0;
            }

            Object scheduleData = response.getBody().getData();

            if (scheduleData instanceof List<?> list) {
                return list.size();
            }

            if (scheduleData instanceof Map<?, ?> map) {
                return map.size();
            }

            return 0;
        } catch (Exception e) {
            log.warn("Schedule fetch failed for {} -> fallback 0", facultyUniversityId, e);
            return 0;
        }
    }

    private List<Map<String, Object>> fetchTodayScheduleSafely(String facultyUniversityId) {
        try {
            ResponseEntity<ApiResponse> response = facultyServiceClient.getSchedule(facultyUniversityId);

            if (response == null || response.getBody() == null || response.getBody().getData() == null) {
                return Collections.emptyList();
            }

            Object scheduleData = response.getBody().getData();
            List<Map<String, Object>> todaySchedule = new ArrayList<>();

            log.info("Raw schedule response for {} => {}", facultyUniversityId, scheduleData);

            if (scheduleData instanceof Map<?, ?> map) {
                for (Object value : map.values()) {
                    if (value != null) {
                        String raw = value.toString().trim();

                        String[] parts = raw.split("\\s+");
                        Map<String, Object> item = new HashMap<>();

                        if (parts.length >= 3) {
                            item.put("time", parts[0] + " - " + parts[1]);
                            item.put("course", parts[2]);
                        } else {
                            item.put("time", raw);
                            item.put("course", "N/A");
                        }

                        item.put("day", LocalDate.now().getDayOfWeek().name());
                        todaySchedule.add(item);
                    }
                }
            }

            log.info("Today schedule for {} => {}", facultyUniversityId, todaySchedule);
            return todaySchedule;

        } catch (Exception e) {
            log.warn("Today schedule fetch failed for {} -> fallback empty list", facultyUniversityId, e);
            return Collections.emptyList();
        }
    }
    private boolean matchesToday(String dayValue, DayOfWeek today) {
        if (dayValue == null || dayValue.isBlank()) {
            return false;
        }

        String day = dayValue.trim().toLowerCase();

        if (day.matches("\\d+")) {
            int numericDay = Integer.parseInt(day);
            return numericDay == today.getValue();
        }

        return switch (today) {
            case MONDAY -> day.equals("monday") || day.equals("mon");
            case TUESDAY -> day.equals("tuesday") || day.equals("tue");
            case WEDNESDAY -> day.equals("wednesday") || day.equals("wed");
            case THURSDAY -> day.equals("thursday") || day.equals("thu");
            case FRIDAY -> day.equals("friday") || day.equals("fri");
            case SATURDAY -> day.equals("saturday") || day.equals("sat");
            case SUNDAY -> day.equals("sunday") || day.equals("sun");
        };
    }
    private void addIfMatchesToday(Object obj, DayOfWeek today, List<Map<String, Object>> todaySchedule) {
        if (!(obj instanceof Map<?, ?> rawMap)) {
            return;
        }

        log.info("Raw schedule item => {}", rawMap);

        String day = firstNonNull(rawMap, "day", "dayOfWeek", "scheduleDay", "lectureDay", "weekDay");
        String course = firstNonNull(rawMap, "course", "courseName", "subjectName");
        String time = firstNonNull(rawMap, "time", "scheduleTime", "slotTime", "startTime");

        if (matchesToday(day, today)) {
            Map<String, Object> item = new HashMap<>();
            item.put("course", course != null ? course : "N/A");
            item.put("time", time != null ? time : "N/A");
            item.put("day", day != null ? day : today.name());
            todaySchedule.add(item);
        }
    }
    private boolean isToday(String dayValue) {
        if (dayValue == null || dayValue.isBlank()) {
            return false;
        }

        String day = dayValue.trim().toLowerCase();
        DayOfWeek today = LocalDate.now().getDayOfWeek();

        return switch (today) {
            case MONDAY -> day.equals("monday") || day.equals("mon");
            case TUESDAY -> day.equals("tuesday") || day.equals("tue");
            case WEDNESDAY -> day.equals("wednesday") || day.equals("wed");
            case THURSDAY -> day.equals("thursday") || day.equals("thu");
            case FRIDAY -> day.equals("friday") || day.equals("fri");
            case SATURDAY -> day.equals("saturday") || day.equals("sat");
            case SUNDAY -> day.equals("sunday") || day.equals("sun");
        };
    }

    private String firstNonNull(Map<?, ?> map, String... keys) {
        for (String key : keys) {
            Object value = map.get(key);
            if (value != null && !value.toString().trim().isEmpty()) {
                return value.toString().trim();
            }
        }
        return null;
    }

    private void validateId(String id, String type) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(type + " ID required");
        }
    }

    private void validateRole(String role) {
        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException("Role required");
        }
    }

    private long extractCount(Object data, String key) {
        if (data instanceof Map<?, ?> map) {
            Object value = map.get(key);
            if (value instanceof Number num) return num.longValue();
        }
        return 0;
    }

    private int extractInt(Object data, String key) {
        if (data instanceof Map<?, ?> map) {
            Object value = map.get(key);
            if (value instanceof Number num) return num.intValue();
        }
        if (data instanceof List<?> list) return list.size();
        return 0;
    }
}