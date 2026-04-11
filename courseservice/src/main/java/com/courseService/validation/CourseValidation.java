package com.collage.courseservice.validation;

import com.collage.courseservice.entity.Course;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CourseValidation {


    private static final int NAME_MIN_LENGTH  = 3;
    private static final int NAME_MAX_LENGTH  = 100;
    private static final int CODE_MIN_LENGTH  = 4;
    private static final int CODE_MAX_LENGTH  = 10;

    /**
     * Allowed course-code pattern: 2-5 uppercase letters followed by 3-6 digits.
     * Examples: CS101, MATH2001, BIO303
     */
    private static final String CODE_PATTERN = "^[A-Z]{2,5}\\d{3,6}$";


    /**
     * Runs all validations and returns a list of error messages.
     * An empty list means the course is valid.
     */
    public List<String> validate(Course course) {
        List<String> errors = new ArrayList<>();

        validateName(course.getName(), errors);
        validateCode(course.getCode(), errors);
        validateFacultyId(course.getFacultyId(), errors);

        return errors;
    }


    /**
     * name:
     *  - must not be null or blank
     *  - length between 3 and 100
     *  - must not contain special characters (letters, digits, spaces only)
     */
    private void validateName(String name, List<String> errors) {
        if (name == null || name.isBlank()) {
            errors.add("Course name is required");
            return;
        }

        String trimmed = name.trim();

        if (trimmed.length() < NAME_MIN_LENGTH) {
            errors.add("Course name must be at least " + NAME_MIN_LENGTH + " characters");
        }

        if (trimmed.length() > NAME_MAX_LENGTH) {
            errors.add("Course name must not exceed " + NAME_MAX_LENGTH + " characters");
        }

        if (!trimmed.matches("^[a-zA-Z0-9 ]+$")) {
            errors.add("Course name must contain only letters, digits, and spaces");
        }
    }

    /**
     * code:
     *  - must not be null or blank
     *  - length between 4 and 10
     *  - must match pattern: 2-5 uppercase letters + 3-6 digits  (e.g. CS101)
     */
    private void validateCode(String code, List<String> errors) {
        if (code == null || code.isBlank()) {
            errors.add("Course code is required");
            return;
        }

        String trimmed = code.trim();

        if (trimmed.length() < CODE_MIN_LENGTH || trimmed.length() > CODE_MAX_LENGTH) {
            errors.add("Course code must be between " + CODE_MIN_LENGTH + " and " + CODE_MAX_LENGTH + " characters");
        }

        if (!trimmed.matches(CODE_PATTERN)) {
            errors.add("Course code must be uppercase letters followed by digits (e.g. CS101, MATH2001)");
        }
    }

    /**
     * facultyId:
     *  - must not be null
     *  - must be a positive number
     */
    private void validateFacultyId(Long facultyId, List<String> errors) {
        if (facultyId == null) {
            errors.add("Faculty ID is required");
            return;
        }

        if (facultyId <= 0) {
            errors.add("Faculty ID must be a positive number");
        }
    }
}