package com.example.demo.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Strategy: sends notifications via SMTP email.
 * Active when app.notification.strategy=email AND app.mail.enabled=true.
 */
@Component
public class EmailNotificationStrategy implements NotificationStrategy {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;

    @Override
    public String getName() {
        return "email";
    }

    @Override
    public void send(String recipient, String subject, String body) {
        if (!mailEnabled || mailSender == null) {
            System.out.printf("[NOTIFICATION][email-disabled] To: %s | %s%n", recipient, subject);
            return;
        }
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(recipient);
            msg.setSubject(subject);
            msg.setText(body);
            mailSender.send(msg);
            System.out.printf("[NOTIFICATION][email-sent] To: %s | %s%n", recipient, subject);
        } catch (Exception e) {
            System.err.println("[NOTIFICATION][email-error] " + e.getMessage());
        }
    }
}
