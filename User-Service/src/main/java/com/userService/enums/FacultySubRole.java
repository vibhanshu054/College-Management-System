package com.userService.enums;

public enum FacultySubRole {
    HOD("Head of Department"),
    PROFESSOR("Professor"),
    ASSISTANT_PROFESSOR("Assistant Professor"),
    TRAINEE("Trainee");

    private final String displayName;

    FacultySubRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}