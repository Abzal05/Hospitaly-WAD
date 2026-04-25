package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Lab 10 — thin wrapper around JavaMailSender.
 * For production notification routing use NotificationService + Strategy pattern instead.
 */
@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;

    @Value("${spring.mail.username:noreply@example.com}")
    private String fromAddress;

    @Async
    public void sendEmail(String to, String subject, String text) {
        if (!mailEnabled || mailSender == null) {
            System.out.printf("[EmailService][disabled] To: %s | Subject: %s%n", to, subject);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            System.out.printf("[EmailService][sent] To: %s | Subject: %s%n", to, subject);
        } catch (Exception e) {
            System.err.println("[EmailService][error] " + e.getMessage());
        }
    }
}
