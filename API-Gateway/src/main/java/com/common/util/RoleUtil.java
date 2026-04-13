package com.common.util;

public class RoleUtil {

    public static boolean isAdmin(String role) {
        return "ADMIN".equals(role);
    }

    public static boolean isFaculty(String role) {
        return "FACULTY".equals(role);
    }

    public static boolean isStudent(String role) {
        return "STUDENT".equals(role);
    }

    public static boolean isLibrarian(String role) {
        return "LIBRARIAN".equals(role);
    }
}