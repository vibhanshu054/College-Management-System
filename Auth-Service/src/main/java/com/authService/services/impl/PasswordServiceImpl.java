package com.authService.services.impl;

import com.authService.dto.*;
import com.authService.entity.OtpEntity;
import com.authService.entity.PasswordResetToken;
import com.authService.exception.*;
import com.authService.repository.OtpRepository;
import com.authService.repository.PasswordTokenRepository;
import com.authService.services.PasswordService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
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
    @Override
    public ApiResponse forgotPassword(String email) {

        log.info("Password reset requested for email: {}", email);
        UserDTO user = null;

        try {
            user = restTemplate.getForObject(
                    "http://USER-SERVICE/api/users/internal/by-email?email=" + email,
                    UserDTO.class
            );
        } catch (HttpClientErrorException.NotFound ex) {
            log.warn("User not found for email: {}", email);
        }

        if (user != null) {

            String token = UUID.randomUUID().toString();

            PasswordResetToken entity = PasswordResetToken.builder()
                    .email(email)
                    .token(token)
                    .expiryTime(LocalDateTime.now().plusMinutes(10))
                    .used(false)
                    .build();

            tokenRepo.save(entity);

            String link = "http://localhost:5173/verify-otp?token=" + token;

            Context context = new Context();
            context.setVariable("username", user.getUsername());
            context.setVariable("email", user.getEmail());
            context.setVariable("resetLink", link);

            String mailBody = templateEngine.process("reset-password-email", context);

            sendMail(email, "Reset Password", mailBody);

            log.info("Password reset token generated and email sent for: {}", email);
        } else {
            log.warn("Password reset requested for non-existing email: {}", email);
        }

        return ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .message("If an account exists, a reset link has been sent")
                .build();
    }

    // 2. GENERATE OTP
    @Override
    public ApiResponse generateOtp(String token) {

        log.info("Starting OTP generation for token: {}", token);

        PasswordResetToken tokenEntity = tokenRepo.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid token"));

        if (tokenEntity.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Token expired");
        }

        if (tokenEntity.isUsed()) {
            throw new InvalidTokenException("Token already used");
        }

        int otpValue = 100000 + random.nextInt(900000);

        OtpEntity otpEntity = OtpEntity.builder()
                .email(tokenEntity.getEmail())
                .token(token)
                .otp(otpValue)
                .expiryTime(LocalDateTime.now().plusMinutes(2))
                .verified(false)
                .build();

        otpRepo.save(otpEntity);

        UserDTO user = restTemplate.getForObject(
                "http://USER-SERVICE/api/users/internal/by-email?email=" + tokenEntity.getEmail(),
                UserDTO.class
        );

        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        Context context = new Context();
        context.setVariable("username", user.getUsername());
        context.setVariable("email", user.getEmail());
        context.setVariable("otp", otpValue);

        String mailBody = templateEngine.process("otp-email", context);

        sendMail(tokenEntity.getEmail(), "Your OTP", mailBody);

        return ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .message("OTP sent successfully")
                .expiryTime(otpEntity.getExpiryTime())
                .build();
    }

    // 3. VERIFY OTP
    @Override
    public ApiResponse verifyOtp(OtpRequestDto request) {

        log.info("Verifying OTP");

        PasswordResetToken token = tokenRepo.findByToken(request.getToken())
                .orElseThrow(() -> new InvalidTokenException("Invalid token"));

        if (token.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Token expired");
        }

        if (token.isUsed()) {
            throw new InvalidTokenException("Token already used");
        }

        OtpEntity otp = otpRepo.findByToken(request.getToken())
                .orElseThrow(() -> new OtpNotFoundException("OTP not found"));

        if (otp.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new OtpExpiredException("OTP expired");
        }

        if (otp.getOtp() != request.getOtp()) {
            throw new InvalidOtpException("Invalid OTP");
        }

        if (otp.isVerified()) {
            throw new InvalidOtpException("OTP already used");
        }

        otp.setVerified(true);
        otpRepo.save(otp);

        return ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .message("OTP verified successfully")
                .build();
    }
    // 4. RESET PASSWORD

    @Override
    @Transactional
    public ApiResponse resetPassword(ResetPasswordRequestDto request) {

        log.info("Resetting password");

        PasswordResetToken token = tokenRepo.findByToken(request.getToken())
                .orElseThrow(() -> new InvalidTokenException("Invalid token"));

        if (token.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Token expired");
        }

        if (token.isUsed()) {
            throw new InvalidTokenException("Token already used");
        }

        OtpEntity otp = otpRepo.findByToken(request.getToken())
                .orElseThrow(() -> new OtpNotFoundException("OTP not found"));

        if (otp.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new OtpExpiredException("OTP expired");
        }

        if (!otp.isVerified()) {
            throw new OtpNotVerifiedException("OTP not verified");
        }

        if (request.getNewPassword() == null || request.getConfirmPassword() == null
                || request.getNewPassword().isBlank() || request.getConfirmPassword().isBlank()) {
            throw new PasswordMismatchException("Password fields cannot be empty");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException("Passwords do not match");
        }

        ApiResponse response = restTemplate.getForObject(
                "http://USER-SERVICE/api/users/internal/by-email?email=" + token.getEmail(),
                ApiResponse.class
        );
        UserDTO user = new ObjectMapper().convertValue(response.getData(), UserDTO.class);


        if (user.getPassword() != null && passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new SamePasswordException("New password cannot be same as old password");
        }

        UpdatePasswordDto dto = new UpdatePasswordDto();
        dto.setEmail(user.getEmail());
        dto.setNewPassword(request.getNewPassword());
        try {
            log.info(" Calling User-Service to reset password for: {}", user.getUsername());
            restTemplate.put(
                    "http://USER-SERVICE/api/users/internal/reset-password",
                    dto);
        } catch (Exception e) {
            log.error(" Failed to reset password in User-Service: {}", e.getMessage());
            throw new RuntimeException("Failed to update password: " + e.getMessage());
        }


        token.setUsed(true);
        tokenRepo.save(token);
        otp.setExpiryTime(LocalDateTime.now());
        otpRepo.save(otp);

        log.info(" Password reset completed for: {}", user.getEmail());
        return ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .message("Password updated successfully")
                .build();
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