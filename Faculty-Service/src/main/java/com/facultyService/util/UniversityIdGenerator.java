package com.facultyService.util;
public class UniversityIdGenerator {

    public static String generate(String prefix, Long number) {
        return prefix + String.format("%06d", number);
    }
}