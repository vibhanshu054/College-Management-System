package com.userService.services.impl;

import com.userService.client.FacultyServiceClient;
import com.userService.client.LibraryServiceClient;
import com.userService.client.StudentServiceClient;
import com.userService.dto.*;
import com.userService.entity.UserEntity;
import com.userService.enums.FacultySubRole;
import com.userService.enums.UserRole;
import com.userService.exception.*;
import com.userService.repository.FacultyRepository;
import com.userService.repository.LibrarianRepository;
import com.userService.repository.StudentRepository;
import com.userService.repository.UserRepository;
import com.userService.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.IllegalArgumentException;
import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final FacultyRepository facultyRepository;
    private final LibrarianRepository librarianRepository;
    private final StudentRepository studentRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final StudentServiceClient studentServiceClient;
    private final FacultyServiceClient facultyServiceClient;
    private final LibraryServiceClient libraryServiceClient;

    // =========================================================
    // CREATE USER
    // =========================================================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDto createUser(UserDto dto, String createdBy, String role) {

        validate(dto);
        log.info("Creating user {}", dto.getEmail());

        if (!"ADMIN".equals(role) && !"SYSTEM".equals(role)) {
            throw new ForbiddenException("Only ADMIN can create users");
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        // SAVE USER
        UserEntity user = modelMapper.map(dto, UserEntity.class);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setCreatedBy(createdBy);
        user.setActive(true);
        UserEntity saved = userRepository.save(user);

        // GENERATE UNIVERSITY ID - UNIFIED APPROACH
        String prefix = switch (dto.getRole().toUpperCase()) {
            case "STUDENT" -> "STU";
            case "FACULTY" -> "FAC";
            case "LIBRARIAN" -> "LIB";
            case "ADMIN" -> "ADM";
            default -> "USR";
        };

        String universityId = prefix + String.format("%06d", saved.getId());
        saved.setUniversityId(universityId);
        log.info("Generated University ID: {} for user: {}", universityId, dto.getEmail());

        userRepository.save(saved);

        // CASCADE CALL
        cascadeCreate(saved);
        log.info("User created successfully with universityId {}", universityId);

        return modelMapper.map(saved, UserDto.class);
    }

    // =========================================================
    // VALIDATION
    // =========================================================
    private void validate(UserDto dto) {
        if (dto == null)
            throw new InvalidOperationException("User data cannot be null");

        if (dto.getName() == null || dto.getName().isBlank())
            throw new InvalidOperationException("Name is required");

        if (dto.getEmail() == null || dto.getEmail().isBlank())
            throw new InvalidOperationException("Email is required");

        if (dto.getUsername() == null || dto.getUsername().isBlank())
            throw new InvalidOperationException("Username is required");

        if (dto.getPassword() == null || dto.getPassword().length() < 8)
            throw new InvalidOperationException("Password must be at least 8 characters");

        if (dto.getRole() == null || dto.getRole().isBlank())
            throw new InvalidOperationException("Role is required");
    }

    // =========================================================
    // DUPLICATE CHECK
    // =========================================================
    private void checkDuplicate(UserDto dto) {

        if (userRepository.findByEmail(dto.getEmail()).isPresent())
            throw new DuplicateResourceException("Email already exists");

        if (dto.getUniversityId() != null &&
                userRepository.findByUniversityId(dto.getUniversityId()).isPresent())
            throw new DuplicateResourceException("University ID already exists");
    }

    // =========================================================
    // BUILD USER
    // =========================================================
    private UserEntity buildUser(UserDto dto, String createdBy) {

        return UserEntity.builder()
                .email(dto.getEmail())
                .username(dto.getUsername())
                .name(dto.getName())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(parseRole(dto.getRole()))
                .department(dto.getDepartment())
                .phoneNumber(dto.getPhoneNumber())
                .universityId(dto.getUniversityId())
                .semester(dto.getSemester())
                .batch(dto.getBatch())
                .courseCode(dto.getCourseCode())
                .facultySubRole(parseFaculty(dto.getFacultySubRole()))
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(createdBy)
                .updatedBy(createdBy)
                .build();
    }

    private UserRole parseRole(String role) {
        try {
            return UserRole.valueOf(role.trim().toUpperCase());
        } catch (Exception e) {
            throw new InvalidOperationException("Invalid role: " + role);
        }
    }

    private FacultySubRole parseFaculty(String role) {
        if (role == null || role.isBlank()) return null;
        return FacultySubRole.valueOf(role.trim().toUpperCase());
    }

    // =========================================================
    // GET METHODS (CLEAN)
    // =========================================================
    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public UserDto getUserById(Long id) {

        return userRepository.findById(id)
                .map(u -> modelMapper.map(u, UserDto.class))
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public UserDto getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .map(u -> modelMapper.map(u, UserDto.class))
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public UserDto getUserByUniversityId(String universityId) {
        if (universityId == null || universityId.isBlank()) {
            throw new IllegalArgumentException("University ID is required");
        }

        UserEntity user = userRepository.findByUniversityId(universityId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with universityId: " + universityId));

        return modelMapper.map(user, UserDto.class);
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<UserDto> getAllUsers(String role) {

        // ROLE VALIDATION
        if (!"ADMIN".equals(role) && !"SYSTEM".equals(role)) {
            throw new ForbiddenException("Only ADMIN allowed");
        }

        return userRepository.findByActiveTrue()
                .stream()
                .map(u -> modelMapper.map(u, UserDto.class))
                .toList();
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<UserDto> getUsersByRole(String role) {

        UserRole userRole = UserRole.valueOf(role.trim().toUpperCase());

        return userRepository.findByRoleAndActiveTrue(userRole)
                .stream()
                .map(u -> modelMapper.map(u, UserDto.class))
                .toList();
    }

    // =========================================================
    // UPDATE
    // =========================================================
    @Override
    public UserDto updateUser(Long id, UserDto dto, String updatedBy, UserRole role) {

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (dto.getUsername() != null) user.setUsername(dto.getUsername());

        if (dto.getName() != null && !dto.getName().isBlank()) user.setName(dto.getName());

        if (dto.getPhoneNumber() != null) user.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getDepartment() != null) user.setDepartment(dto.getDepartment());
        if (dto.getEmail() != null &&
                !dto.getEmail().equalsIgnoreCase(user.getEmail())) {

            if (userRepository.findByEmail(dto.getEmail()).isPresent())
                throw new DuplicateResourceException("Email already exists");
            cascadeUpdate(user);
            user.setEmail(dto.getEmail());
        }

        user.setUpdatedBy(updatedBy);
        user.setUpdatedAt(LocalDateTime.now());
        cascadeUpdate(user);

        return modelMapper.map(userRepository.save(user), UserDto.class);
    }

    // =========================================================
    // DELETE
    // =========================================================
    @Override
    public void deleteUser(Long id, String deletedBy, String role) {

        if (!"ADMIN".equals(role) && !"SYSTEM".equals(role)) {
            throw new ForbiddenException("Only ADMIN can delete users");
        }

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        cascadeDelete(user);

        user.setActive(false);
        user.setUpdatedBy(deletedBy);
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    // =========================================================
    // PASSWORD
    // =========================================================
    @Override
    public void updatePassword(Long id, String oldPwd, String newPwd, String updatedBy) {

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(oldPwd, user.getPassword()))
            throw new InvalidOperationException("Old password incorrect");

        if (newPwd == null || newPwd.length() < 8)
            throw new InvalidOperationException("Weak password");

        user.setPassword(passwordEncoder.encode(newPwd));
        user.setUpdatedBy(updatedBy);
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    // =========================================================
    // VERIFY
    // =========================================================
    @Override
    public UserDto verifyCredentials(String email, String password) {

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.isActive())
            throw new InvalidOperationException("Inactive user");

        if (!passwordEncoder.matches(password, user.getPassword()))
            throw new InvalidOperationException("Invalid credentials");

        return modelMapper.map(user, UserDto.class);
    }


    @Override
    public void deleteUserByUniversityId(String universityId, String deletedBy, String role) {

        if (!"ADMIN".equals(role) && !"SYSTEM".equals(role)) {
            throw new ForbiddenException("Only ADMIN can delete users");
        }

        UserEntity user = userRepository.findByUniversityId(universityId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with universityId: " + universityId));

        cascadeDelete(user);

        user.setActive(false);
        user.setUpdatedBy(deletedBy);
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    @Override
    public UserDto updateUserByUniversityId(String universityId, UserDto dto, String updatedBy) {

        UserEntity user = userRepository.findByUniversityId(universityId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with universityId: " + universityId));

        if (dto.getUsername() != null) user.setUsername(dto.getUsername());
        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getPhoneNumber() != null) user.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getDepartment() != null) user.setDepartment(dto.getDepartment());

        user.setUpdatedBy(updatedBy);
        user.setUpdatedAt(LocalDateTime.now());

        cascadeUpdate(user);

        return modelMapper.map(userRepository.save(user), UserDto.class);
    }


    @Override
    public void updatePasswordByUniversityId(
            String universityId,
            String oldPwd,
            String newPwd,
            String updatedBy
    ) {

        UserEntity user = userRepository.findByUniversityId(universityId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(oldPwd, user.getPassword())) {
            throw new InvalidOperationException("Old password incorrect");
        }

        if (newPwd == null || newPwd.length() < 8) {
            throw new InvalidOperationException("Password must be at least 8 characters");
        }

        user.setPassword(passwordEncoder.encode(newPwd));
        user.setUpdatedBy(updatedBy);
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    @Override
    public void resetPasswordByEmail(String email, String newPassword) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found by email"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public void resetPasswordByUsername(String username, String newPassword) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found by username"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public long getUserCountByRole(String role) {

        try {
            UserRole userRole = UserRole.valueOf(role.trim().toUpperCase());
            return userRepository.countByRoleAndActiveTrue(userRole);

        } catch (Exception e) {
            throw new InvalidOperationException("Invalid role: " + role);
        }
    }

    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public long getUserCountByRoleAndDepartment(String role, String department) {

        if (department == null || department.isBlank()) {
            throw new InvalidOperationException("Department cannot be empty");
        }

        try {
            UserRole userRole = UserRole.valueOf(role.trim().toUpperCase());
            return userRepository.countByRoleAndDepartmentAndActiveTrue(userRole, department);

        } catch (Exception e) {
            throw new InvalidOperationException("Invalid role or department");
        }
    }

    // =========================================================
    // CASCADE CREATE - FIXED VERSION
    // =========================================================
    private void cascadeCreate(UserEntity user) {
        try {
            switch (user.getRole()) {

                case STUDENT -> {
                    log.info("Cascading STUDENT creation for universityId: {}", user.getUniversityId());

                    // Build StudentDTO with ALL required fields
                    StudentDTO dto = StudentDTO.builder()
                            .studentName(user.getUsername())
                            .studentEmail(user.getEmail())
                            .studentPhoneNumber(user.getPhoneNumber())
                            .universityId(user.getUniversityId())
                            .semester(user.getSemester() != null ? user.getSemester() : "1")
                            .batch(user.getBatch() != null ? user.getBatch() : "2024-2028")  // Realistic default
                            .department(user.getDepartment())
                            .course(user.getCourseCode() != null ? user.getCourseCode() : "NOT_ASSIGNED")
                            .courseCode(user.getCourseCode() != null ? user.getCourseCode() : "NA")
                            .active(true)
                            .build();

                    log.debug("Calling Student-Service to create student: {}", dto);

                    try {
                        ResponseEntity<StudentDTO> res = studentServiceClient.createStudentFromUser(dto);

                        if (res != null && res.getStatusCode().is2xxSuccessful() && res.getBody() != null) {
                            user.setStudentServiceUniversityId(user.getUniversityId());
                            log.info(" Student created in Student-Service with ID: {}", user.getUniversityId());
                        } else {
                            log.error(" Student-Service returned unsuccessful response: {}",
                                    res != null ? res.getStatusCode() : "NULL");
                            throw new RuntimeException("Student service failed");
                        }
                    } catch (Exception e) {
                        log.error(" CASCADE_CREATE FAILED for student: {}", user.getEmail(), e);
                        throw e;
                    }
                }

                case FACULTY -> {
                    log.info("Cascading FACULTY creation for universityId: {}", user.getUniversityId());

                    FacultyDTO dto = FacultyDTO.builder()
                            .facultyName(user.getUsername())
                            .facultyEmail(user.getEmail())
                            .facultyPhoneNumber(user.getPhoneNumber())
                            .facultyUniversityId(user.getUniversityId())
                            .department(user.getDepartment())
                            .subRole(user.getFacultySubRole() != null
                                    ? user.getFacultySubRole().name()
                                    : "TRAINEE")
                            .active(true)
                            .build();

                    try {
                        ResponseEntity<ApiResponse> res = facultyServiceClient.createFacultyFromUser(dto);

                        if (res != null && res.getStatusCode().is2xxSuccessful() && res.getBody() != null) {
                            user.setFacultyServiceUniversityId(user.getUniversityId());
                            log.info(" Faculty created in Faculty-Service with ID: {}", user.getUniversityId());
                        } else {
                            log.error(" Faculty-Service returned unsuccessful response");
                            throw new RuntimeException("Faculty service failed");
                        }
                    } catch (Exception e) {
                        log.error(" CASCADE_CREATE FAILED for faculty: {}", user.getEmail(), e);
                        throw e;
                    }
                }

                case LIBRARIAN -> {
                    log.info("Cascading LIBRARIAN creation for universityId: {}", user.getUniversityId());

                    LibrarianDTO dto = LibrarianDTO.builder()
                            .librarianName(user.getUsername())
                            .librarianEmail(user.getEmail())
                            .librarianPhoneNumber(user.getPhoneNumber())
                            .universityId(user.getUniversityId())
                            .active(true)
                            .build();

                    try {
                        ResponseEntity<LibrarianDTO> res = libraryServiceClient.createLibrarianFromUser(dto);

                        if (res != null && res.getStatusCode().is2xxSuccessful() && res.getBody() != null) {
                            user.setLibrarianServiceUniversityId(user.getUniversityId());
                            log.info(" Librarian created in Library-Service with ID: {}", user.getUniversityId());
                        } else {
                            log.error(" Library-Service returned unsuccessful response");
                            throw new RuntimeException("Librarian service failed");
                        }
                    } catch (Exception e) {
                        log.error(" CASCADE_CREATE FAILED for librarian: {}", user.getEmail(), e);
                        throw e;
                    }
                }

                case ADMIN -> {
                    log.info(" ADMIN - no cascade required");
                }
            }

            // Save user with cascade IDs
            userRepository.save(user);
            log.info(" User cascade creation completed for: {}", user.getUniversityId());

        } catch (Exception e) {
            log.error(" CASCADE CREATE FAILED for user: {}", user.getEmail(), e);
            throw new RuntimeException("Failed to sync services: " + e.getMessage(), e);
        }
    }

    // =========================================================
// CASCADE UPDATE - FIXED VERSION
// =========================================================
    private void cascadeUpdate(UserEntity user) {
        log.info("CASCADE UPDATE START for user: {}", user.getUniversityId());

        try {
            switch (user.getRole()) {

                case STUDENT -> {
                    try {
                        StudentDTO dto = StudentDTO.builder()
                                .studentName(user.getUsername())
                                .studentEmail(user.getEmail())
                                .studentPhoneNumber(user.getPhoneNumber())
                                .department(user.getDepartment())
                                .semester(user.getSemester())
                                .batch(user.getBatch())
                                .courseCode(user.getCourseCode())
                                .build();

                        studentServiceClient.updateStudent(user.getUniversityId(), dto);

                        log.info(" [CASCADE_UPDATE] Student updated: {}", user.getUniversityId());

                    } catch (Exception e) {
                        log.warn(" [CASCADE_UPDATE] Student update failed (non-critical) for: {}",
                                user.getUniversityId(), e.getMessage());
                    }
                }

                case FACULTY -> {
                    try {
                        FacultyDTO dto = FacultyDTO.builder()
                                .facultyName(user.getUsername())
                                .facultyEmail(user.getEmail())
                                .facultyPhoneNumber(user.getPhoneNumber())
                                .facultyUniversityId(user.getUniversityId())
                                .department(user.getDepartment())
                                .subRole(user.getFacultySubRole() != null
                                        ? user.getFacultySubRole().name()
                                        : "TRAINEE")
                                .build();

                        facultyServiceClient.updateFaculty(user.getUniversityId(), dto);

                        log.info(" [CASCADE_UPDATE] Faculty updated: {}", user.getUniversityId());

                    } catch (Exception e) {
                        log.warn(" [CASCADE_UPDATE] Faculty update failed (non-critical) for: {}",
                                user.getUniversityId(), e.getMessage());
                    }
                }

                case LIBRARIAN -> {
                    try {
                        LibrarianDTO dto = LibrarianDTO.builder()
                                .librarianName(user.getUsername())
                                .librarianEmail(user.getEmail())
                                .librarianPhoneNumber(user.getPhoneNumber())
                                .build();

                        libraryServiceClient.updateLibrarian(user.getUniversityId(), dto);

                        log.info("✓ [CASCADE_UPDATE] Librarian updated: {}", user.getUniversityId());

                    } catch (Exception e) {
                        log.warn("⚠ [CASCADE_UPDATE] Librarian update failed (non-critical) for: {}",
                                user.getUniversityId(), e);
                    }
                }

                case ADMIN -> {
                    log.info("⬇ [CASCADE_UPDATE] ADMIN - no cascade required");
                }
            }

            log.info(" CASCADE UPDATE completed for user: {}", user.getUniversityId());

        } catch (Exception e) {
            log.error(" [CASCADE_UPDATE] Unexpected failure for user: {}", user.getUniversityId(), e);
            throw new RuntimeException("Update sync failed: " + e.getMessage(), e);
        }
    }

    // =========================================================
// CASCADE DELETE - FIXED VERSION
// =========================================================
    private void cascadeDelete(UserEntity user) {
        log.info("CASCADE DELETE START for user: {}", user.getUniversityId());

        try {
            switch (user.getRole()) {

                case STUDENT -> {
                    if (user.getStudentServiceUniversityId() != null) {
                        try {
                            studentServiceClient.deleteStudent(user.getUniversityId());
                            log.info(" Student deleted from Student-Service: {}", user.getUniversityId());
                        } catch (Exception e) {
                            log.warn(" Student deletion failed (non-critical): {}", e.getMessage());
                        }
                    }
                }

                case FACULTY -> {
                    if (user.getFacultyServiceUniversityId() != null) {
                        try {
                            facultyServiceClient.deleteFaculty(user.getUniversityId());
                            log.info(" Faculty deleted from Faculty-Service: {}", user.getUniversityId());
                        } catch (Exception e) {
                            log.warn(" Faculty deletion failed (non-critical): {}", e.getMessage());
                        }
                    }
                }

                case LIBRARIAN -> {
                    if (user.getLibrarianServiceUniversityId() != null) {
                        libraryServiceClient.deleteLibrarian(user.getUniversityId());
                        log.info("✓ Librarian deleted from Library-Service: {}", user.getUniversityId());
                    }
                }
                case ADMIN -> {
                    log.info(" ADMIN deletion - no cascade required");
                }
            }

            log.info(" CASCADE DELETE completed for user: {}", user.getUniversityId());

        } catch (Exception e) {
            log.error(" CASCADE DELETE FAILED for user: {}", user.getUniversityId(), e);
            // Don't re-throw - allow user deletion even if cascade fails
            log.warn(" Continuing with user deletion despite cascade error");
        }
    }
}