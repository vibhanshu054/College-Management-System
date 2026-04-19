package com.facultyService.service.impl;

import com.facultyService.client.*;
import com.facultyService.dto.ApiResponse;
import com.facultyService.dto.FacultyDTO;
import com.facultyService.entity.FacultyCourseMapping;
import com.facultyService.entity.FacultyEntity;
import com.facultyService.enums.FacultySubRole;
import com.facultyService.repository.FacultyCourseMappingRepository;
import com.facultyService.repository.FacultyRepository;
import com.facultyService.service.FacultyService;
import com.student.enums.AttendanceStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FacultyServiceImpl implements FacultyService {

    private final FacultyRepository facultyRepository;
    private final FacultyCourseMappingRepository mappingRepository;
    private final ModelMapper modelMapper;
    private final UserServiceClient userServiceClient;
    private final StudentClient studentClient;
    private final LibraryClient libraryClient;
    private final CourseClient courseClient;
    private final AttendanceClient attendanceClient;

    @Override
    public ApiResponse createFaculty(FacultyDTO dto) {

        if (dto.getFacultyUniversityId() == null || dto.getFacultyUniversityId().isBlank()) {
            throw new RuntimeException("Faculty University ID is required");
        }

        FacultyEntity entity = new FacultyEntity();
        entity.setFacultyUniversityId(dto.getFacultyUniversityId());
        entity.setFacultyName(dto.getFacultyName());
        entity.setFacultyEmail(dto.getFacultyEmail());
        entity.setFacultyPhoneNumber(dto.getFacultyPhoneNumber());
        entity.setDepartment(dto.getDepartment());
        entity.setSubRole(
                dto.getSubRole() != null
                        ? FacultySubRole.valueOf(String.valueOf(dto.getSubRole()))
                        : FacultySubRole.TRAINEE
        );
        entity.setActive(true);

        facultyRepository.save(entity);

        return new ApiResponse(
                "Faculty created successfully",
                201,
                entity,
                LocalDateTime.now()
        );
    }

    @Override
    public ApiResponse getFaculty(String universityId) {
        FacultyEntity faculty = getEntity(universityId);

        return new ApiResponse(
                "Faculty fetched",
                200,
                modelMapper.map(faculty, FacultyDTO.class),
                LocalDateTime.now()
        );
    }

    @Override
    public ApiResponse getFacultyByFacultyUniversityId(String universityId) {

        if (universityId == null || universityId.isBlank()) {
            throw new IllegalArgumentException("UniversityId required");
        }

        FacultyEntity faculty = facultyRepository.findByFacultyUniversityId(universityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Faculty not found"));

        return new ApiResponse(
                "Faculty fetched",
                200,
                modelMapper.map(faculty, FacultyDTO.class),
                LocalDateTime.now()
        );
    }

    @Override
    public ApiResponse getAllFaculty(String department, String subRole) {

        List<FacultyEntity> list = facultyRepository.findAll();

        if (department != null && !department.isBlank()) {
            list = list.stream()
                    .filter(f -> department.equalsIgnoreCase(f.getDepartment()))
                    .toList();
        }

        if (subRole != null && !subRole.isBlank()) {
            list = list.stream()
                    .filter(f -> f.getSubRole() != null && subRole.equalsIgnoreCase(f.getSubRole().name()))
                    .toList();
        }

        return new ApiResponse(
                "Faculty list fetched",
                200,
                list.stream().map(f -> modelMapper.map(f, FacultyDTO.class)).toList(),
                LocalDateTime.now()
        );
    }

    @Override
    public ApiResponse updateFaculty(String universityId, FacultyDTO dto) {

        FacultyEntity faculty = getEntity(universityId);
        modelMapper.map(dto, faculty);
        faculty.setUpdatedAt(LocalDateTime.now());
        facultyRepository.save(faculty);

        return new ApiResponse("Faculty updated", 200, null, LocalDateTime.now());
    }

    @Override
    public ApiResponse deleteFaculty(String universityId) {

        log.info("Deleting faculty {}", universityId);

        FacultyEntity faculty = getEntity(universityId);

        try { studentClient.removeFacultyFromStudents(universityId); } catch (Exception e) { log.warn("Student fail"); }
        try { courseClient.removeFacultyCourses(universityId); } catch (Exception e) { log.warn("Course fail"); }
        try { libraryClient.removeFacultyRecords(universityId); } catch (Exception e) { log.warn("Library fail"); }
        try { userServiceClient.deactivateUser(universityId); } catch (Exception e) { log.warn("User fail"); }

        mappingRepository.deleteAll(mappingRepository.findByFacultyUniversityId(universityId));
        facultyRepository.delete(faculty);

        return new ApiResponse("Faculty deleted", 200, null, LocalDateTime.now());
    }

    @Override
    public ApiResponse getDashboard(String universityId) {

        FacultyEntity faculty = getEntity(universityId);
        String uid = faculty.getFacultyUniversityId();

        Map<String, Object> res = new HashMap<>();
        res.put("faculty", modelMapper.map(faculty, FacultyDTO.class));
        res.put("students",
                Optional.ofNullable(studentClient.getStudentsByFacultyUniversityId(uid))
                        .orElse(new ApiResponse("Students fetched", 200, new ArrayList<>(), LocalDateTime.now())));
        res.put("courses", getAssignedCourses(uid).getData());
        res.put("attendance", getAttendance(uid).getData());

        return new ApiResponse("Dashboard fetched", 200, res, LocalDateTime.now());
    }

    @Override
    public ApiResponse getFacultyDashboard(String universityId) {
        log.info("Fetching faculty dashboard for {}", universityId);
        return getDashboard(universityId);
    }

    @Override
    public ApiResponse getCoursesById(String universityId) {

        log.info("Fetching courses for faculty {}", universityId);

        List<Map<String, Object>> courses =
                (List<Map<String, Object>>) getAssignedCourses(getEntity(universityId).getFacultyUniversityId()).getData();

        return new ApiResponse(
                "Courses fetched",
                200,
                courses,
                LocalDateTime.now()
        );
    }

    @Override
    public ApiResponse assignCoursesById(String universityId, List<Long> courseIds) {

        if (courseIds == null || courseIds.isEmpty()) {
            throw new IllegalArgumentException("Course list required");
        }

        log.info("Assigning courses {} to faculty {}", courseIds, universityId);

        courseClient.assignCoursesToFaculty(universityId, courseIds);

        for (Long courseId : courseIds) {
            String courseCode = String.valueOf(courseId);

            if (mappingRepository.existsByFacultyUniversityIdAndCourseCode(universityId, courseCode)) {
                continue;
            }

            FacultyCourseMapping mapping = new FacultyCourseMapping();
            mapping.setFacultyUniversityId(universityId);
            mapping.setCourseCode(courseCode);
            mapping.setCourseName(null);

            mappingRepository.save(mapping);
        }

        return new ApiResponse("Courses assigned", 200, null, LocalDateTime.now());
    }

    @Override
    public ApiResponse assignCourses(String universityId, List<Long> courseIds) {

        log.info("Assigning courses to faculty {}", universityId);

        if (universityId == null || universityId.isBlank() || courseIds == null || courseIds.isEmpty()) {
            throw new IllegalArgumentException("Invalid request");
        }

        for (Long courseId : courseIds) {
            String courseCode = String.valueOf(courseId);

            if (mappingRepository.existsByFacultyUniversityIdAndCourseCode(universityId, courseCode)) {
                throw new IllegalArgumentException("Duplicate course");
            }

            FacultyCourseMapping mapping = new FacultyCourseMapping();
            mapping.setFacultyUniversityId(universityId);
            mapping.setCourseCode(courseCode);
            mapping.setCourseName(null);

            mappingRepository.save(mapping);
        }

        courseClient.assignCoursesToFaculty(universityId, courseIds);

        return new ApiResponse("Courses assigned", 200, null, LocalDateTime.now());
    }

    @Override
    public ApiResponse getAssignedCourses(String universityId) {
        List<FacultyCourseMapping> mappings = mappingRepository.findByFacultyUniversityId(universityId);

        List<Map<String, Object>> result = mappings.stream()
                .map(m -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("courseCode", m.getCourseCode());
                    map.put("courseName", m.getCourseName());
                    return map;
                })
                .toList();

        return new ApiResponse("Courses fetched", 200, result, LocalDateTime.now());
    }

    @Override
    public ApiResponse getStudentsById(String universityId) {

        ApiResponse response;

        try {
            response = studentClient.getStudentsByFacultyUniversityId(universityId);
        } catch (Exception e) {
            log.error("Student service failed, returning empty list");
            response = new ApiResponse("Fallback students", 200, new ArrayList<>(), LocalDateTime.now());
        }

        List<?> students = response.getData() != null ? (List<?>) response.getData() : new ArrayList<>();

        return new ApiResponse("Students fetched", 200, students, LocalDateTime.now());
    }

    @Override
    public ApiResponse getStudents(String universityId) {

        log.info("Fetching students for faculty {}", universityId);

        if (universityId == null || universityId.isBlank()) {
            throw new IllegalArgumentException("universityId required");
        }

        ApiResponse response = studentClient.getStudentsByFacultyUniversityId(universityId);
        List<?> students = response.getData() != null ? (List<?>) response.getData() : new ArrayList<>();

        return new ApiResponse("Students fetched", 200, students, LocalDateTime.now());
    }

    @Override
    public ApiResponse getTotalStudents(String universityId) {

        ApiResponse response = getStudents(universityId);
        List<?> list = response.getData() != null ? (List<?>) response.getData() : new ArrayList<>();

        return new ApiResponse("Total students", 200, Map.of("count", list.size()), LocalDateTime.now());
    }

    @Override
    public ApiResponse getStudentCountById(String universityId) {
        return getTotalStudents(universityId);
    }

    @Override
    public ApiResponse getAttendanceById(String universityId) {
        return new ApiResponse("Attendance fetched", 200, getAttendance(universityId), LocalDateTime.now());
    }

    @Override
    public ApiResponse updateBookStatsByFacultyUniversityId(String facultyUniversityId, int issued, int returned) {

        FacultyEntity faculty = getEntity(facultyUniversityId);

        if (faculty.getBooksIssued() + issued - returned < 0) {
            throw new IllegalArgumentException("Invalid book operation");
        }
        if (issued < 0 || returned < 0) {
            throw new IllegalArgumentException("Invalid values");
        }

        faculty.setBooksIssued(faculty.getBooksIssued() + issued);
        faculty.setBooksReturned(faculty.getBooksReturned() + returned);

        facultyRepository.save(faculty);

        return new ApiResponse("Book stats updated", 200, null, LocalDateTime.now());
    }

    @Override
    public ApiResponse getAttendance(String universityId) {

        log.info("Fetching attendance for faculty {}", universityId);

        if (universityId == null || universityId.isBlank()) {
            throw new IllegalArgumentException("universityId required");
        }

        List<Map<String, Object>> list =
                attendanceClient.getFacultyAttendance(
                        universityId,
                        LocalDate.now().minusDays(30).toString(),
                        LocalDate.now().toString());

        long present = list.stream()
                .filter(a -> "PRESENT".equalsIgnoreCase(String.valueOf(a.get("status"))))
                .count();

        double percentage = list.isEmpty() ? 0 : (present * 100.0) / list.size();

        Map<String, Object> data = Map.of(
                "percentage", percentage,
                "present", present,
                "absent", list.size() - present
        );

        return new ApiResponse("Attendance fetched", 200, data, LocalDateTime.now());
    }

    @Override
    public ApiResponse assignScheduleById(String universityId, Map<String, Object> schedule) {
        return assignSchedule(getEntity(universityId).getFacultyUniversityId(), schedule);
    }

    @Override
    public ApiResponse assignSchedule(String universityId, Map<String, Object> schedule) {

        log.info("Assigning schedule for faculty {}", universityId);

        FacultyEntity faculty = facultyRepository.findByFacultyUniversityId(universityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Faculty not found"));

        Map<String, Object> existing =
                Optional.ofNullable(faculty.getScheduleData()).orElse(new HashMap<>());

        existing.putAll(schedule);

        faculty.setScheduleData(existing);
        facultyRepository.save(faculty);

        return new ApiResponse("Schedule updated", 200, null, LocalDateTime.now());
    }

    @Override
    public ApiResponse markAttendance(String universityId,
                                      AttendanceStatus status,
                                      Long facultyId,
                                      String courseCode) {

        log.info("Delegating attendance to STUDENT-SERVICE | studentId: {}", universityId);

        if (status == null || universityId == null || courseCode == null) {
            throw new IllegalArgumentException("Invalid attendance request");
        }

        try {
            ApiResponse response = studentClient.markAttendance(
                    universityId,
                    status.name(),
                    facultyId,
                    courseCode
            );

            log.info("Attendance marked via student-service");
            return response;

        } catch (Exception e) {
            log.error("Feign call failed for attendance", e);
            throw new RuntimeException("Attendance service unavailable");
        }
    }

    @Override
    public ApiResponse markFacultySelfAttendance(String universityId, Long facultyId) {

        log.info("Delegating faculty self attendance");

        try {
            return studentClient.markAttendance(universityId, "PRESENT", facultyId, "SELF-ATTENDANCE");
        } catch (Exception e) {
            log.error("Feign failed", e);
            throw new RuntimeException("Attendance failed");
        }
    }

    @Override
    public ApiResponse getSchedule(String universityId) {
        return new ApiResponse(
                "Schedule fetched",
                200,
                getEntity(universityId).getScheduleData(),
                LocalDateTime.now()
        );
    }

    @Override
    public ApiResponse getAttendanceCalendar(String universityId) {
        return new ApiResponse(
                "Attendance calendar fetched",
                200,
                getEntity(universityId).getAttendanceCalendar(),
                LocalDateTime.now()
        );
    }

    @Override
    public ApiResponse updateSchedule(String universityId, Map<String, Object> scheduleData) {
        return assignScheduleById(universityId, scheduleData);
    }

    private FacultyEntity getEntity(String universityId) {
        return facultyRepository.findByFacultyUniversityId(universityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Faculty not found"));
    }
}