package com.library.exception;

public class MissingServletRequestParameterException extends RuntimeException {
    public MissingServletRequestParameterException(String message) {
        super(message);
    }

    public String getParameterName() {
        // Extract the parameter name from the message
        String[] parts = getMessage().split("'");
        if (parts.length >= 2) {
            return parts[1]; // The parameter name is between the first pair of single quotes
        }
        return null; // Return null if the parameter name cannot be extracted
    }
}
