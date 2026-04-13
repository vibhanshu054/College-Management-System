package com.authService.services.impl;

import com.authService.dto.OtpRequestDto;
import com.authService.dto.ResetPasswordRequestDto;
import com.authService.dto.UpdatePasswordDto;
import com.authService.dto.UserDTO;
import com.authService.entity.OtpEntity;
import com.authService.entity.PasswordResetToken;
import com.authService.exception.*;
import com.authService.repository.OtpRepository;
import com.authService.repository.PasswordTokenRepository;
import com.authService.services.PasswordService;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordServiceImpl implements PasswordService {

    private final PasswordTokenRepository tokenRepo;
    private final OtpRepository otpRepo;
    private final JavaMailSender mailSender;
    private final RestTemplate restTemplate;
    private final PasswordEncoder passwordEncoder;
    private final SpringTemplateEngine templateEngine;
    private static final SecureRandom random = new SecureRandom();

    // 1. FORGOT PASSWORD
    public String forgotPassword(String email) {

        log.info("Password reset requested for email: {}", email);
        UserDTO user = null;
        //CHECK USER EXIST WITH EMAIL
        try {
            user = restTemplate.getForObject(
                    "http://USER-SERVICE/api/users/internal/by-email?email=" + email,
                    UserDTO.class
            );
        } catch (HttpClientErrorException.NotFound ex) {
            log.warn("User not found for email: {}", email);
            // user remains null, proceed accordingly
        }

        // USER SHOULD NOT = NULL
        if (user != null) {

            String token = UUID.randomUUID().toString();

            PasswordResetToken entity = PasswordResetToken.builder()
                    .email(email)
                    .token(token)
                    .expiryTime(LocalDateTime.now().plusMinutes(10))
                    .used(false)
                    .build();

            tokenRepo.save(entity);

            String link = "http://localhost:8080/auth/reset?token=" + token;


            // Build email body with user information
            Context context = new Context();
            context.setVariable("username", user.getUsername());
            context.setVariable("email", user.getEmail());
            context.setVariable("resetLink", link);

            // Process template into HTML string
            String mailBody = templateEngine.process("reset-password-email", context);

            sendMail(email, "Reset Password", mailBody);

            log.info("Password reset token generated and email sent for: {}", email);
        } else {
            log.warn("Password reset requested for non-existing email: {}", email);
        }

        return "If an account exists, a reset link has been sent";
    }

    // 2. GENERATE OTP
    public String generateOtp(String token) {

        log.info("Starting OTP generation for token: {}", token);

        // 1. Fetch the token entity
        PasswordResetToken tokenEntity = tokenRepo.findByToken(token)
                .orElseThrow(() -> {
                    log.error("Invalid token: {}", token);
                    return new InvalidTokenException("Invalid token");
                });

        log.info("Found token entity for email: {}", tokenEntity.getEmail());

        // 2. Validate token expiry and usage
        if (tokenEntity.getExpiryTime().isBefore(LocalDateTime.now())) {
            log.warn("Token expired for email: {}", tokenEntity.getEmail());
            throw new InvalidTokenException("Token expired");
        }

        if (tokenEntity.isUsed()) {
            log.warn("Token already used for email: {}", tokenEntity.getEmail());
            throw new InvalidTokenException("Token already used");
        }

        log.info("Token is valid. Generating OTP...");

        // 3. Generate OTP
        int otpValue = 100000 + random.nextInt(900000);

        OtpEntity otpEntity = OtpEntity.builder()
                .email(tokenEntity.getEmail())
                .token(token)
                .otp(otpValue)
                .expiryTime(LocalDateTime.now().plusMinutes(2))
                .verified(false)
                .build();

        otpRepo.save(otpEntity);
        log.info("OTP saved in database for email: {} | OTP: {}", tokenEntity.getEmail(), otpValue);

        // 4. Fetch user by email (fail fast if not found)
        UserDTO user = restTemplate.getForObject(
                "http://USER-SERVICE/api/users/internal/by-email?email=" + tokenEntity.getEmail(),
                UserDTO.class
        );

        if (user == null) {
            log.error("User not found for email: {}", tokenEntity.getEmail());
            throw new UserNotFoundException("User not found for email: " + tokenEntity.getEmail());
        }

        log.info("Fetched user details: username={}, email={}", user.getUsername(), user.getEmail());

        // 5. Build email body
        Context context = new Context();
        context.setVariable("username", user.getUsername());
        context.setVariable("email", user.getEmail());
        context.setVariable("otp", otpValue);

        String mailBody = templateEngine.process("otp-email", context);

        // 6. Send email
        try {
            sendMail(tokenEntity.getEmail(), "Your OTP", mailBody);
            log.info("OTP email successfully sent to: {}", tokenEntity.getEmail());
        } catch (Exception e) {
            log.error("Failed to send OTP email to: {}", tokenEntity.getEmail(), e);
            throw new RuntimeException("Failed to send OTP email");
        }

        log.info("OTP generation completed for email: {}", tokenEntity.getEmail());

        return "OTP sent";
    }

    // 3. VERIFY OTP
    public String verifyOtp(OtpRequestDto request) {

        log.info("Verifying OTP for token");

        PasswordResetToken token = tokenRepo.findByToken(request.getToken())
                .orElseThrow(() -> new InvalidTokenException("Invalid token"));

        // Validate token
        if (token.getExpiryTime().isBefore(LocalDateTime.now())) {
            log.warn("OTP expired for token: {}", request.getToken());
            throw new InvalidTokenException("Token expired");
        }

        if (token.isUsed()) {
            throw new InvalidTokenException("Token already used");
        }

        OtpEntity otp = otpRepo.findByToken(request.getToken())
                .orElseThrow(() -> new OtpNotFoundException("OTP not found"));

        // Validate OTP
        if (otp.getExpiryTime().isBefore(LocalDateTime.now())) {

            throw new OtpExpiredException("OTP expired");
        }

        if (otp.getOtp() != request.getOtp()) {
            log.warn("Invalid OTP provided for token: {}", request.getToken());
            throw new InvalidOtpException("Invalid OTP");
        }

        if (otp.isVerified()) {
            log.warn("OTP already used for token: {}", request.getToken());
            throw new InvalidOtpException("OTP already used");
        }

        otp.setVerified(true);
        otpRepo.save(otp);

        log.info("OTP successfully verified for token");

        return "OTP verified";
    }

    // 4. RESET PASSWORD
    @Transactional
    public String resetPassword(ResetPasswordRequestDto request) {

        log.info("Resetting password using token");

        // 1. Validate token
        PasswordResetToken token = tokenRepo.findByToken(request.getToken())
                .orElseThrow(() -> new InvalidTokenException("Invalid token"));

        if (token.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Token expired");
        }

        if (token.isUsed()) {
            throw new InvalidTokenException("Token already used");
        }

        // 2. Validate OTP
        OtpEntity otp = otpRepo.findByToken(request.getToken())
                .orElseThrow(() -> new OtpNotFoundException("OTP not found"));

        if (otp.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new OtpExpiredException("OTP expired");
        }

        if (!otp.isVerified()) {
            throw new OtpNotVerifiedException("OTP not verified");
        }

        // 3. Validate passwords
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException("Passwords do not match");
        }

        // 4. Fetch user
        UserDTO user = restTemplate.getForObject(
                "http://USER-SERVICE/api/users/internal/by-email?email=" + token.getEmail(),
                UserDTO.class
        );

        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        log.info("Attempting to reset password for user: {}", user.getUsername());
        // 5. Prevent reuse of old password
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            log.warn("New password is same as old password for user: {}", user.getUsername());
            throw new SamePasswordException("New password cannot be same as old password");
        }

        // 6. Update password via User Service
        UpdatePasswordDto dto = new UpdatePasswordDto();
        dto.setUsername(user.getUsername());
        dto.setNewPassword(request.getNewPassword());

        restTemplate.postForObject(
                "http://User-Service/users/update-password",
                dto,
                String.class
        );
        log.info("Password updated via User Service for user: {}", user.getUsername());

        // 7. Mark token as used
        token.setUsed(true);
        tokenRepo.save(token);

        // 8. Invalidate OTP after successful reset
        otp.setExpiryTime(LocalDateTime.now());
        otpRepo.save(otp);

        log.info("Password successfully updated for user: {}", user.getUsername());

        return "Password updated successfully";
    }

    // MAIL SENDER
    private void sendMail(String to, String subject, String content) {

        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            log.info("Sending email to: {} with subject: {}", to, subject);
            helper.setText(content, true);
            mailSender.send(message);

        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
            throw new RuntimeException("Mail sending failed");
        }
    }
}