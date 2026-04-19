package com.student.service.impl;

import com.student.client.SubjectServiceClient;
import com.student.client.UserServiceClient;
import com.student.dto.ApiResponse;
import com.student.dto.CourseStudentCountProjection;
import com.student.dto.StudentDTO;
import com.student.dto.UserDto;
import com.student.entity.AttendanceRecord;
import com.student.entity.StudentEntity;
import com.student.enums.AttendanceStatus;
import com.student.exception.DuplicateStudentException;
import com.student.exception.ForbiddenException;
import com.student.exception.ResourceNotFoundException;
import com.student.repository.AttendanceRepository;
import com.student.repository.StudentRepository;
import com.student.service.StudentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final SubjectServiceClient subjectServiceClient;
    private final AttendanceRepository attendanceRepository;
    private final ModelMapper modelMapper;
    private final UserServiceClient userServiceClient;

    private static final String ADMIN = "ADMIN";
    private static final String SYSTEM = "SYSTEM";
    private static final String FACULTY = "FACULTY";

    private void validateAdminOrSystem(String role, String message) {
        if (!ADMIN.equalsIgnoreCase(role) && !SYSTEM.equalsIgnoreCase(role)) {
            throw new ForbiddenException(message);
        }
    }

    private void validateFacultyOrAdmin(String role, String message) {
        if (!FACULTY.equalsIgnoreCase(role) && !ADMIN.equalsIgnoreCase(role)) {
            throw new ForbiddenException(message);
        }
    }

    private void validateStudentDtoForCreate(StudentDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Student data is required");
        }
        if (dto.getStudentName() == null || dto.getStudentName().isBlank()) {
            throw new IllegalArgumentException("Student name is required");
        }
        if (dto.getStudentEmail() == null || dto.getStudentEmail().isBlank()) {
            throw new IllegalArgumentException("Student email is required");
        }
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (dto.getStudentPhoneNumber() == null || dto.getStudentPhoneNumber().isBlank()) {
            throw new IllegalArgumentException("Student phone number is required");
        }
        if (dto.getSemester() == null || dto.getSemester().isBlank()) {
            throw new IllegalArgumentException("Semester is required");
        }
        if (dto.getBatch() == null || dto.getBatch().isBlank()) {
            throw new IllegalArgumentException("Batch is required");
        }
        if (dto.getDepartment() == null || dto.getDepartment().isBlank()) {
            throw new IllegalArgumentException("Department is required");
        }
        if (dto.getCourse() == null || dto.getCourse().isBlank()) {
            throw new IllegalArgumentException("Course is required");
        }
        if (dto.getCourseCode() == null || dto.getCourseCode().isBlank()) {
            throw new IllegalArgumentException("Course code is required");
        }
        if (dto.getFacultyUniversityId() == null || dto.getFacultyUniversityId().isBlank()) {
            throw new IllegalArgumentException("Faculty universityId is required");
        }
    }

    private List<String> sanitizeSubjects(List<String> subjects) {
        if (subjects == null) {
            return List.of();
        }

        return subjects.stream()
                .filter(subject -> subject != null && !subject.isBlank())
                .map(String::trim)
                .distinct()
                .toList();
    }

    private StudentDTO convertToDto(StudentEntity student) {
        StudentDTO dto = modelMapper.map(student, StudentDTO.class);
        dto.setPassword(null);
        return dto;
    }

    @Override
    public ApiResponse getStudentsByFacultyUniversityId(String facultyUniversityId) {
        if (facultyUniversityId == null || facultyUniversityId.isBlank()) {
            throw new IllegalArgumentException("Faculty universityId is required");
        }

        List<StudentDTO> students = studentRepository.findByFacultyUniversityId(facultyUniversityId)
                .stream()
                .map(this::convertToDto)
                .toList();

        return new ApiResponse("Students fetched", 200, students, LocalDateTime.now());
    }

    @Override
    public ApiResponse createStudent(StudentDTO dto, String createdBy, String role) {

        log.info("CREATE STUDENT START | email={}", dto.getStudentEmail());

        validateAdminOrSystem(role, "Only ADMIN or SYSTEM can create student");
        validateStudentDtoForCreate(dto);

        String email = dto.getStudentEmail().trim().toLowerCase();

        if (studentRepository.findByStudentEmail(email).isPresent()) {
            throw new DuplicateStudentException("Student already exists with email: " + email);
        }

        //  FACULTY VALIDATION (IMPORTANT FIX)
        ResponseEntity<ApiResponse> facultyRes =
                userServiceClient.getUserByUniversityId(dto.getFacultyUniversityId());

        if (facultyRes == null || facultyRes.getBody() == null || facultyRes.getBody().getData() == null) {
            throw new ResourceNotFoundException(
                    "Faculty not found with universityId: " + dto.getFacultyUniversityId()
            );
        }

        UserDto faculty = modelMapper.map(facultyRes.getBody().getData(), UserDto.class);

        //  ENTITY BUILD
        StudentEntity student = StudentEntity.builder()
                .studentName(dto.getStudentName().trim())
                .studentEmail(email)
                .password(dto.getPassword()) //  RAW password (as per your requirement)
                .studentPhoneNumber(dto.getStudentPhoneNumber().trim())
                .semester(dto.getSemester().trim())
                .batch(dto.getBatch().trim())
                .department(dto.getDepartment().trim())
                .course(dto.getCourse().trim())
                .courseCode(dto.getCourseCode().trim().toUpperCase())
                .facultyUniversityId(dto.getFacultyUniversityId().trim())
                .facultyName(faculty.getName())
                .subjects(sanitizeSubjects(dto.getSubjects()))
                .createdBy(createdBy)
                .updatedBy(createdBy)
                .active(true)
                .booksIssued(0)
                .booksReturned(0)
                .attendancePercentage(0.0f)
                .build();

        StudentEntity saved = studentRepository.save(student);

        //  AUTO UNIVERSITY ID
        String universityId = "STU" + String.format("%06d", saved.getId());
        saved.setUniversityId(universityId);
        saved = studentRepository.save(saved);

        //  CASCADE TO USER SERVICE
        try {
            UserDto userDto = UserDto.builder()
                    .email(saved.getStudentEmail())
                    .username(saved.getStudentName())
                    .name(saved.getStudentName())
                    .password(saved.getPassword())
                    .role("STUDENT")
                    .department(saved.getDepartment())
                    .phoneNumber(saved.getStudentPhoneNumber())
                    .universityId(saved.getUniversityId())
                    .semester(saved.getSemester())
                    .batch(saved.getBatch())
                    .courseCode(saved.getCourseCode())
                    .build();

            userServiceClient.createUser(userDto);

            log.info("CASCADE USER SUCCESS for {}", saved.getUniversityId());

        } catch (Exception e) {
            log.error("CASCADE USER FAILED", e);
            throw new RuntimeException("User service sync failed");
        }

        return new ApiResponse(
                "Student created successfully",
                201,
                convertToDto(saved),
                LocalDateTime.now()
        );
    }

    @Override
    public ApiResponse getStudent(Long id) {
        StudentEntity student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        return new ApiResponse("Student fetched", 200, convertToDto(student), LocalDateTime.now());
    }

    @Override
    public ApiResponse getStudentByUniversityId(String universityId) {
        if (universityId == null || universityId.isBlank()) {
            throw new IllegalArgumentException("University ID is required");
        }

        StudentEntity student = studentRepository.findByUniversityId(universityId.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with university ID: " + universityId));

        return new ApiResponse("Student fetched", 200, convertToDto(student), LocalDateTime.now());
    }

    @Override
    public ApiResponse getAllStudents(String courseCode, String semester, String department, String role) {
        validateAdminOrSystem(role, "Only ADMIN or SYSTEM allowed");

        List<StudentEntity> students;

        boolean hasCourseCode = courseCode != null && !courseCode.isBlank();
        boolean hasSemester = semester != null && !semester.isBlank();
        boolean hasDepartment = department != null && !department.isBlank();

        if (hasCourseCode && hasSemester && hasDepartment) {
            students = studentRepository.findByCourseCodeAndSemesterAndDepartmentAndActiveTrue(
                    courseCode.trim(),
                    semester.trim(),
                    department.trim()
            );
        } else if (hasCourseCode && hasSemester) {
            students = studentRepository.findByCourseCodeAndSemesterAndActiveTrue(
                    courseCode.trim(),
                    semester.trim()
            );
        } else if (hasCourseCode && hasDepartment) {
            students = studentRepository.findByCourseCodeAndDepartmentAndActiveTrue(
                    courseCode.trim(),
                    department.trim()
            );
        } else if (hasSemester && hasDepartment) {
            students = studentRepository.findBySemesterAndDepartmentAndActiveTrue(
                    semester.trim(),
                    department.trim()
            );
        } else if (hasCourseCode) {
            students = studentRepository.findByCourseCodeAndActiveTrue(courseCode.trim());
        } else if (hasSemester) {
            students = studentRepository.findBySemesterAndActiveTrue(semester.trim());
        } else if (hasDepartment) {
            students = studentRepository.findByDepartmentAndActiveTrue(department.trim());
        } else {
            students = studentRepository.findByActiveTrue();
        }

        List<StudentDTO> studentDtos = students.stream()
                .map(this::convertToDto)
                .toList();

        return new ApiResponse("Students fetched", 200, studentDtos, LocalDateTime.now());
    }

    @Override
    public ApiResponse updateStudent(String universityId, StudentDTO studentDto, String updatedBy, String role) {
        validateAdminOrSystem(role, "Only ADMIN or SYSTEM can update");

        if (studentDto == null) {
            throw new IllegalArgumentException("Student data is required");
        }

        StudentEntity student = studentRepository.findByUniversityId(universityId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        if (studentDto.getStudentName() != null && !studentDto.getStudentName().isBlank()) {
            student.setStudentName(studentDto.getStudentName().trim());
        }

        if (studentDto.getStudentEmail() != null && !studentDto.getStudentEmail().isBlank()) {
            String newEmail = studentDto.getStudentEmail().trim().toLowerCase();
            studentRepository.findByStudentEmail(newEmail)
                    .filter(existing -> !existing.getUniversityId().equals(student.getUniversityId()))
                    .ifPresent(existing -> {
                        throw new DuplicateStudentException("Student email already exists");
                    });
            student.setStudentEmail(newEmail);
        }

        if (studentDto.getPassword() != null && !studentDto.getPassword().isBlank()) {
            student.setPassword(studentDto.getPassword().trim());
        }

        if (studentDto.getStudentPhoneNumber() != null && !studentDto.getStudentPhoneNumber().isBlank()) {
            student.setStudentPhoneNumber(studentDto.getStudentPhoneNumber().trim());
        }

        if (studentDto.getSemester() != null && !studentDto.getSemester().isBlank()) {
            student.setSemester(studentDto.getSemester().trim());
        }

        if (studentDto.getBatch() != null && !studentDto.getBatch().isBlank()) {
            student.setBatch(studentDto.getBatch().trim());
        }

        if (studentDto.getDepartment() != null && !studentDto.getDepartment().isBlank()) {
            student.setDepartment(studentDto.getDepartment().trim());
        }

        if (studentDto.getCourse() != null && !studentDto.getCourse().isBlank()) {
            student.setCourse(studentDto.getCourse().trim());
        }

        if (studentDto.getCourseCode() != null && !studentDto.getCourseCode().isBlank()) {
            student.setCourseCode(studentDto.getCourseCode().trim().toUpperCase());
        }

        if (studentDto.getFacultyUniversityId() != null && !studentDto.getFacultyUniversityId().isBlank()) {
            ResponseEntity<ApiResponse> facultyResponse =
                    userServiceClient.getUserByUniversityId(studentDto.getFacultyUniversityId().trim());

            if (facultyResponse == null || facultyResponse.getBody() == null || facultyResponse.getBody().getData() == null) {
                throw new ResourceNotFoundException(
                        "Faculty not found with universityId: " + studentDto.getFacultyUniversityId()
                );
            }

            UserDto facultyDto = modelMapper.map(facultyResponse.getBody().getData(), UserDto.class);
            student.setFacultyUniversityId(studentDto.getFacultyUniversityId().trim());
            student.setFacultyName(facultyDto.getName());
        }

        if (studentDto.getSubjects() != null) {
            student.setSubjects(sanitizeSubjects(studentDto.getSubjects()));
        }

        student.setUpdatedBy(updatedBy);

        StudentEntity saved = studentRepository.save(student);
        cascadeUpdate(saved);

        return new ApiResponse("Student updated", 200, convertToDto(saved), LocalDateTime.now());
    }

    @Override
    public ApiResponse deleteStudent(String universityId, String deletedBy, String role) {
        validateAdminOrSystem(role, "Only ADMIN or SYSTEM can delete");

        StudentEntity student = studentRepository.findByUniversityId(universityId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        student.setActive(false);
        student.setUpdatedBy(deletedBy);

        StudentEntity saved = studentRepository.save(student);
        cascadeDelete(saved);

        return new ApiResponse("Student deleted successfully", 200, null, LocalDateTime.now());
    }

    @Override
    public ApiResponse getStudentDashboard(String universityId) {
        StudentEntity student = studentRepository.findByUniversityId(universityId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("profile", convertToDto(student));
        dashboard.put("booksIssued", student.getBooksIssued());
        dashboard.put("booksReturned", student.getBooksReturned());
        dashboard.put("attendancePercentage", student.getAttendancePercentage());
        dashboard.put("totalAttendanceRecords", attendanceRepository.countByStudentUniversityId(universityId));

        return new ApiResponse("Dashboard fetched", 200, dashboard, LocalDateTime.now());
    }

    @Override
    public ApiResponse markAttendance(String universityId, AttendanceStatus status, Long facultyId, String courseCode) {
        if (universityId == null || universityId.isBlank()) {
            throw new IllegalArgumentException("University ID is required");
        }
        if (status == null) {
            throw new IllegalArgumentException("Attendance status is required");
        }
        if (facultyId == null) {
            throw new IllegalArgumentException("Faculty ID is required");
        }
        if (courseCode == null || courseCode.isBlank()) {
            throw new IllegalArgumentException("Course code is required");
        }

        StudentEntity student = studentRepository.findByUniversityId(universityId.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + universityId));

        AttendanceRecord record = new AttendanceRecord();
        record.setStudentId(student.getId());
        record.setStudentUniversityId(student.getUniversityId());
        record.setStudentName(student.getStudentName());
        record.setCourseCode(courseCode.trim().toUpperCase());
        record.setAttendanceDate(LocalDate.now());
        record.setStatus(status);
        record.setFacultyId(facultyId);
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());

        attendanceRepository.save(record);
        recalculateAttendancePercentage(universityId);

        return new ApiResponse("Attendance marked successfully", 200, null, LocalDateTime.now());
    }

    @Override
    public void recalculateAttendancePercentage(String universityId) {
        StudentEntity student = studentRepository.findByUniversityId(universityId).orElse(null);
        if (student == null) {
            return;
        }

        long totalRecords = attendanceRepository.countByStudentUniversityId(universityId);
        if (totalRecords == 0) {
            student.setAttendancePercentage(0.0f);
            studentRepository.save(student);
            return;
        }

        long presentCount = attendanceRepository.countByStudentUniversityIdAndStatus(
                universityId,
                AttendanceStatus.PRESENT
        );

        float percentage = (float) (presentCount * 100.0 / totalRecords);
        student.setAttendancePercentage(percentage);
        studentRepository.save(student);
    }

    @Override
    public ApiResponse getAttendanceCalendar(String universityId) {
        List<Map<String, Object>> records = attendanceRepository
                .findByStudentUniversityIdOrderByAttendanceDateDesc(universityId)
                .stream()
                .map(record -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("date", record.getAttendanceDate());
                    map.put("status", record.getStatus());
                    map.put("courseCode", record.getCourseCode());
                    map.put("facultyId", record.getFacultyId());
                    return map;
                })
                .toList();

        return new ApiResponse("Attendance fetched", 200, records, LocalDateTime.now());
    }

    @Override
    public void incrementBooksIssued(String universityId) {
        StudentEntity student = studentRepository.findByUniversityId(universityId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + universityId));

        int currentIssued = student.getBooksIssued() == null ? 0 : student.getBooksIssued();
        if (currentIssued >= 5) {
            throw new IllegalArgumentException("Book limit exceeded");
        }

        student.setBooksIssued(currentIssued + 1);
        studentRepository.save(student);
    }

    @Override
    public void incrementBooksReturned(String universityId) {
        StudentEntity student = studentRepository.findByUniversityId(universityId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + universityId));

        int currentIssued = student.getBooksIssued() == null ? 0 : student.getBooksIssued();
        int currentReturned = student.getBooksReturned() == null ? 0 : student.getBooksReturned();

        if (currentIssued <= 0) {
            throw new IllegalArgumentException("No issued books available to return");
        }

        student.setBooksIssued(currentIssued - 1);
        student.setBooksReturned(currentReturned + 1);
        studentRepository.save(student);
    }

    @Override
    public ApiResponse getStudentsByCourse(String courseCode) {
        List<StudentDTO> students = studentRepository.findByCourseCodeAndActiveTrue(courseCode)
                .stream()
                .map(this::convertToDto)
                .toList();

        return new ApiResponse("Students fetched by course", 200, students, LocalDateTime.now());
    }

    @Override
    public ApiResponse getTotalStudentsCount() {
        long count = studentRepository.countByActiveTrue();
        return new ApiResponse("Total students count", 200, Map.of("totalStudents", count), LocalDateTime.now());
    }

    @Override
    public ApiResponse getStudentsCountByCourse(String courseCode) {
        long count = studentRepository.countByCourseCodeAndActiveTrue(courseCode);
        return new ApiResponse("Students count by course", 200, Map.of("count", count), LocalDateTime.now());
    }

    @Override
    public ApiResponse getStudentsByDepartment(String department) {
        List<StudentDTO> students = studentRepository.findByDepartmentAndActiveTrue(department)
                .stream()
                .map(this::convertToDto)
                .toList();

        return new ApiResponse("Students fetched by department", 200, students, LocalDateTime.now());
    }

    @Override
    public ApiResponse getStudentsCountByDepartment(String department) {
        long count = studentRepository.countByDepartmentAndActiveTrue(department);
        return new ApiResponse("Students count by department", 200, Map.of("count", count), LocalDateTime.now());
    }

    @Override
    public ApiResponse getBooksStatus(String universityId) {
        StudentEntity student = studentRepository.findByUniversityId(universityId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + universityId));

        Map<String, Integer> booksStatus = new HashMap<>();
        booksStatus.put("booksIssued", student.getBooksIssued());
        booksStatus.put("booksReturned", student.getBooksReturned());

        return new ApiResponse("Books status fetched", 200, booksStatus, LocalDateTime.now());
    }

    @Override
    public ApiResponse updateBookStats(String universityId, int issued, int returned) {
        StudentEntity student = studentRepository.findByUniversityId(universityId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        if (issued < 0 || returned < 0) {
            throw new IllegalArgumentException("Issued and returned values cannot be negative");
        }

        int currentIssued = student.getBooksIssued() == null ? 0 : student.getBooksIssued();
        int currentReturned = student.getBooksReturned() == null ? 0 : student.getBooksReturned();

        int newIssued = currentIssued + issued - returned;
        if (newIssued < 0) {
            throw new IllegalArgumentException("Returned books cannot exceed currently issued books");
        }

        if (newIssued > 5) {
            throw new IllegalArgumentException("Book limit exceeded");
        }

        student.setBooksIssued(newIssued);
        student.setBooksReturned(currentReturned + returned);
        studentRepository.save(student);

        Map<String, Integer> response = new HashMap<>();
        response.put("booksIssued", student.getBooksIssued());
        response.put("booksReturned", student.getBooksReturned());

        return new ApiResponse("Book stats updated", 200, response, LocalDateTime.now());
    }

    @Override
    public ApiResponse updateSubjects(String universityId, List<String> subjects, String role) {

        log.info("UPDATE SUBJECTS | student={}", universityId);

        validateFacultyOrAdmin(role, "Only faculty/admin can assign subjects");

        List<String> cleaned = sanitizeSubjects(subjects);

        if (cleaned.isEmpty()) {
            throw new IllegalArgumentException("Subjects list cannot be empty");
        }

        StudentEntity student = studentRepository.findByUniversityId(universityId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        //  SUBJECT VALIDATION FROM SUBJECT SERVICE
        for (String code : cleaned) {
            ResponseEntity<ApiResponse> res = subjectServiceClient.getSubjectByCode(code);

            if (res == null || res.getBody() == null|| res.getBody().getData()==null) {
                throw new ResourceNotFoundException("Invalid subject code: " + code);
            }
        }

        student.setSubjects(cleaned);
        studentRepository.save(student);

        return new ApiResponse(
                "Subjects assigned successfully",
                200,
                cleaned,
                LocalDateTime.now()
        );
    }

    @Override
    public ApiResponse getStudentCountByCourse() {
        List<CourseStudentCountProjection> data = studentRepository.getStudentCountGroupByCourse();

        return new ApiResponse(
                "Course wise student count fetched successfully",
                200,
                data,
                LocalDateTime.now()
        );
    }

    @Override
    public ApiResponse updateSemester(String universityId, String semester, String role) {
        validateFacultyOrAdmin(role, "Only faculty/admin can update semester");

        if (semester == null || semester.isBlank()) {
            throw new IllegalArgumentException("Semester is required");
        }

        StudentEntity student = studentRepository.findByUniversityId(universityId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        student.setSemester(semester.trim());
        studentRepository.save(student);

        return new ApiResponse("Semester updated", 200, convertToDto(student), LocalDateTime.now());
    }

    private void cascadeCreate(StudentEntity student) {
        try {
            log.info("CASCADE CREATE START for {}", student.getUniversityId());
            log.info("CASCADE CREATE SUCCESS");
        } catch (Exception e) {
            log.error("CASCADE CREATE FAILED", e);
            throw new RuntimeException("Cascade create failed");
        }
    }

    private void cascadeUpdate(StudentEntity student) {
        try {
            UserDto userDto = UserDto.builder()
                    .email(student.getStudentEmail())
                    .username(student.getStudentName())
                    .name(student.getStudentName())
                    .password(student.getPassword())
                    .department(student.getDepartment())
                    .phoneNumber(student.getStudentPhoneNumber())
                    .courseCode(student.getCourseCode())
                    .semester(student.getSemester())
                    .batch(student.getBatch())
                    .build();

            userServiceClient.updateUserByUniversityId(student.getUniversityId(), userDto);

            log.info("CASCADE UPDATE SUCCESS");

        } catch (Exception e) {
            log.error("CASCADE UPDATE FAILED", e);
        }
    }

    private void cascadeDelete(StudentEntity student) {
        try {
            log.info("CASCADE DELETE START for {}", student.getUniversityId());

            userServiceClient.deleteUser(student.getUniversityId());

            log.info("CASCADE DELETE SUCCESS");

        } catch (Exception e) {
            log.error("CASCADE DELETE FAILED", e);
        }
    }
}