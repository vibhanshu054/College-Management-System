package com.authService.util;

import lombok.Getter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Getter
@Component
public class PasswordEncoderUtil {

    /**
     * -- GETTER --
     *  Get the password encoder instance
     */
    private final BCryptPasswordEncoder encoder;

    public PasswordEncoderUtil() {
        this.encoder = new BCryptPasswordEncoder(12); // Strength: 12
    }

    /**
     * Encode a plain text password
     */
    public String encodePassword(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    /**
     * Verify if a raw password matches the encoded password
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }

}
