package com.authService.Config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {
@Bean
@ConditionalOnProperty(name = "spring.mail.host")
public JavaMailSender mailSender() {
    return new JavaMailSenderImpl();
}
}