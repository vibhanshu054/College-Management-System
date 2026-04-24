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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

        // Check if already exists
        if (facultyRepository.findByFacultyUniversityId(dto.getFacultyUniversityId()).isPresent()) {
            throw new RuntimeException("Faculty already exists with this university ID");
        }

        FacultyEntity entity = new FacultyEntity();
        entity.setFacultyUniversityId(dto.getFacultyUniversityId());  // Use provided ID from cascade
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

        log.info(" Faculty created with University ID: {}", dto.getFacultyUniversityId());

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
                convertToDto(faculty),
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
                convertToDto(faculty),
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
                    .filter(f -> f.getSubRole() != null &&
                            subRole.equalsIgnoreCase(f.getSubRole().name()))
                    .toList();
        }

        List<FacultyDTO> result = list.stream()
                .map(this::convertToDto)
                .toList();

        return new ApiResponse(
                "Faculty list fetched",
                200,
                result,
                LocalDateTime.now()
        );
    }

    @Override
    public ApiResponse updateFaculty(String universityId, FacultyDTO dto) {

        log.info("⬇ [CASCADE_UPDATE] START updating Faculty | universityId={}", universityId);

        FacultyEntity faculty = getEntity(universityId);

        // Update all fields from DTO
        if (dto.getFacultyName() != null && !dto.getFacultyName().isBlank()) {
            faculty.setFacultyName(dto.getFacultyName().trim());
            log.info("✓ Faculty name updated: {}", dto.getFacultyName());
        }

        if (dto.getFacultyEmail() != null && !dto.getFacultyEmail().isBlank()) {
            faculty.setFacultyEmail(dto.getFacultyEmail().trim());
            log.info("✓ Faculty email updated: {}", dto.getFacultyEmail());
        }

        if (dto.getFacultyPhoneNumber() != null && !dto.getFacultyPhoneNumber().isBlank()) {
            faculty.setFacultyPhoneNumber(dto.getFacultyPhoneNumber().trim());
            log.info("✓ Faculty phone updated: {}", dto.getFacultyPhoneNumber());
        }

        if (dto.getDepartment() != null && !dto.getDepartment().isBlank()) {
            faculty.setDepartment(dto.getDepartment().trim());
            log.info("✓ Faculty department updated: {}", dto.getDepartment());
        }

        if (dto.getSubRole() != null) {
            faculty.setSubRole(dto.getSubRole());
            log.info("✓ Faculty sub-role updated: {}", dto.getSubRole());
        }

        if (dto.getActive() != null) {
            faculty.setActive(dto.getActive());
            log.info("✓ Faculty active status updated: {}", dto.getActive());
        }

        FacultyEntity updated = facultyRepository.save(faculty);

        // ========================================================================
        // CASCADE UPDATE TO LIBRARY SERVICE - UPDATE ALL ISSUED BOOKS/HISTORY
        // ========================================================================
        try {
            log.info("⬇ [CASCADE_UPDATE_TO_LIBRARY] Syncing faculty details to library...");

            // Fetch all book issues for this faculty
            ResponseEntity<ApiResponse> bookIssuesResponse =
                    (ResponseEntity<ApiResponse>) libraryClient.getFacultyBooks(universityId);

            if (bookIssuesResponse != null && bookIssuesResponse.getBody() != null) {
                Map<String, Object> bookData = (Map<String, Object>) bookIssuesResponse.getBody().getData();

                log.info("✓ [CASCADE_UPDATE_TO_LIBRARY] Found {} issued books for faculty",
                        bookData != null ? bookData.size() : 0);

                // Note: Library service will internally update all BookIssueEntity records
                // with new faculty details through stored procedures or batch updates

                log.info("✓ [CASCADE_UPDATE_TO_LIBRARY] Faculty book records synced successfully");
            }
        } catch (Exception e) {
            log.warn("⚠ [CASCADE_UPDATE_TO_LIBRARY] Library sync failed (non-critical) for faculty {}: {}",
                    universityId, e.getMessage());
            // Don't throw - library update is non-critical
        }

        log.info("✓ [CASCADE_UPDATE] Faculty update completed | universityId={}", universityId);

        return new ApiResponse("✓ Faculty updated successfully", 200, convertToDto(faculty), LocalDateTime.now());
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
        res.put("faculty", convertToDto(faculty));
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
    @Transactional
    public ApiResponse assignCoursesByCourseCode(String facultyUniversityId, List<String> courseCodes) {

        log.info("START assignCoursesByCourseCode | facultyUniversityId={} | incomingCourseCodes={}",
                facultyUniversityId, courseCodes);

        try {
            if (facultyUniversityId == null || facultyUniversityId.isBlank()) {
                log.error("Validation failed: facultyUniversityId is null or blank");
                throw new IllegalArgumentException("Faculty universityId is required");
            }

            if (courseCodes == null || courseCodes.isEmpty()) {
                log.error("Validation failed: courseCodes is null or empty for faculty={}", facultyUniversityId);
                throw new IllegalArgumentException("Course list required");
            }

            List<String> normalizedCodes = courseCodes.stream()
                    .filter(code -> code != null && !code.isBlank())
                    .map(String::trim)
                    .distinct()
                    .toList();

            log.info("Normalized course codes for faculty {} => {}", facultyUniversityId, normalizedCodes);

            if (normalizedCodes.isEmpty()) {
                log.error("Validation failed: no valid course codes after normalization for faculty={}", facultyUniversityId);
                throw new IllegalArgumentException("Valid course codes required");
            }

            FacultyEntity faculty = getEntity(facultyUniversityId);
            log.info("Faculty found | id={} | universityId={} | name={} | currentTotalCourses={} | currentCourseCodes={}",
                    faculty.getId(),
                    faculty.getFacultyUniversityId(),
                    faculty.getFacultyName(),
                    faculty.getTotalCourses(),
                    faculty.getCourseCode());

            log.info("Fetching all courses from course service for faculty={}", facultyUniversityId);

            ResponseEntity<ApiResponse> courseResponseEntity = courseClient.getAllCourses();
            ApiResponse courseResponse = courseResponseEntity.getBody();

            log.info("Course service HTTP status={}", courseResponseEntity.getStatusCode());

            if (courseResponse == null) {
                log.error("Course service returned null body for faculty={}", facultyUniversityId);
                throw new RuntimeException("Course service returned empty response");
            }

            List<Map<String, Object>> allCourses =
                    courseResponse.getData() != null
                            ? (List<Map<String, Object>>) courseResponse.getData()
                            : new ArrayList<>();

            log.info("Course service raw response message={} | status={}",
                    courseResponse.getMessage(),
                    courseResponse.getStatus());

            log.info("Course service returned {} courses for validation", allCourses.size());

            int assigned = 0;
            int skipped = 0;

            for (String code : normalizedCodes) {
                log.info("Processing courseCode={} for faculty={}", code, facultyUniversityId);

                boolean alreadyExists = mappingRepository
                        .existsByFacultyUniversityIdAndCourseCode(facultyUniversityId, code);

                if (alreadyExists) {
                    skipped++;
                    log.warn("Skipping duplicate mapping | facultyUniversityId={} | courseCode={}",
                            facultyUniversityId, code);
                    continue;
                }

                Map<String, Object> matchedCourse = allCourses.stream()
                        .filter(course -> code.equalsIgnoreCase(String.valueOf(course.get("courseCode"))))
                        .findFirst()
                        .orElseThrow(() -> {
                            log.error("Course not found in course-service response | facultyUniversityId={} | courseCode={}",
                                    facultyUniversityId, code);
                            return new IllegalArgumentException("Course not found: " + code);
                        });

                log.info("Matched course | facultyUniversityId={} | courseCode={} | courseName={}",
                        facultyUniversityId,
                        matchedCourse.get("courseCode"),
                        matchedCourse.get("name") != null ? matchedCourse.get("name") : matchedCourse.get("courseName"));

                FacultyCourseMapping mapping = new FacultyCourseMapping();
                mapping.setFacultyUniversityId(facultyUniversityId);
                mapping.setCourseCode(code);
                mapping.setCourseName(
                        String.valueOf(
                                matchedCourse.get("name") != null
                                        ? matchedCourse.get("name")
                                        : matchedCourse.get("courseName")
                        )
                );

                FacultyCourseMapping savedMapping = mappingRepository.save(mapping);

                log.info("Mapping saved successfully | mappingId={} | facultyUniversityId={} | courseCode={} | courseName={}",
                        savedMapping.getId(),
                        savedMapping.getFacultyUniversityId(),
                        savedMapping.getCourseCode(),
                        savedMapping.getCourseName());

                assigned++;
            }

            log.info("Calling course service to assign courses | facultyUniversityId={} | courseCodes={}",
                    facultyUniversityId, normalizedCodes);

            ResponseEntity<ApiResponse> assignResponseEntity =
                    courseClient.assignCoursesToFacultyByCode(facultyUniversityId, normalizedCodes);

            ApiResponse assignResponse = assignResponseEntity.getBody();

            log.info("Course assignment service HTTP status={}", assignResponseEntity.getStatusCode());

            if (assignResponse == null) {
                log.error("Course assignment response body is null for faculty={}", facultyUniversityId);
                throw new RuntimeException("Course assignment failed: empty response");
            }

            log.info("Course assignment completed successfully for faculty={} | message={}",
                    facultyUniversityId, assignResponse.getMessage());

            log.info("Fetching saved mappings from DB for faculty={}", facultyUniversityId);
            List<FacultyCourseMapping> facultyMappings =
                    mappingRepository.findByFacultyUniversityId(facultyUniversityId);

            log.info("Total mappings found in DB for faculty {} => {}", facultyUniversityId, facultyMappings.size());

            List<String> assignedCourseCodes = facultyMappings.stream()
                    .map(FacultyCourseMapping::getCourseCode)
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();

            log.info("Final assigned course codes from DB for faculty {} => {}", facultyUniversityId, assignedCourseCodes);

            faculty.setCourseCode(assignedCourseCodes);
            faculty.setTotalCourses(assignedCourseCodes.size());

            log.info("Updating faculty entity | facultyUniversityId={} | totalCourses={} | courseCodes={}",
                    facultyUniversityId,
                    assignedCourseCodes.size(),
                    assignedCourseCodes);

            FacultyEntity updatedFaculty = facultyRepository.save(faculty);

            log.info("Faculty updated successfully | facultyUniversityId={} | dbTotalCourses={} | dbCourseCodes={}",
                    updatedFaculty.getFacultyUniversityId(),
                    updatedFaculty.getTotalCourses(),
                    updatedFaculty.getCourseCode());

            Map<String, Object> responseData = Map.of(
                    "facultyUniversityId", facultyUniversityId,
                    "courseCodes", assignedCourseCodes,
                    "totalCourses", assignedCourseCodes.size(),
                    "assigned", assigned,
                    "skipped", skipped
            );

            log.info("END assignCoursesByCourseCode SUCCESS | facultyUniversityId={} | response={}",
                    facultyUniversityId, responseData);

            return new ApiResponse(
                    "Courses assigned successfully",
                    200,
                    responseData,
                    LocalDateTime.now()
            );

        } catch (Exception e) {
            log.error("END assignCoursesByCourseCode FAILED | facultyUniversityId={} | incomingCourseCodes={} | error={}",
                    facultyUniversityId, courseCodes, e.getMessage(), e);
            throw e;
        }
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
    private FacultyDTO convertToDto(FacultyEntity entity) {
        FacultyDTO dto = modelMapper.map(entity, FacultyDTO.class);

        List<FacultyCourseMapping> mappings =
                mappingRepository.findByFacultyUniversityId(entity.getFacultyUniversityId());

        List<String> courseCodes = mappings.stream()
                .map(FacultyCourseMapping::getCourseCode)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        dto.setCourseCode(courseCodes);
        dto.setTotalCourse(courseCodes.size());
        dto.setPassword(null);

        return dto;
    }
}