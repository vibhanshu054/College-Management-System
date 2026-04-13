package com.userService.enums;


    public enum UserRole {
        ADMIN("Admin"),
        FACULTY("Faculty"),
        LIBRARIAN("Librarian"),
        STUDENT("Student");

        private final String displayName;

        UserRole(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }