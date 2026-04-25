package com.example.demo.controller.api;

import com.example.demo.service.EmailService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Lab 10 — REST endpoint for manual email sending (admin only).
 * POST /email/send  body: { "to": "...", "subject": "...", "text": "..." }
 */
@RestController
@RequestMapping("/email")
@PreAuthorize("hasRole('ADMIN')")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    public Map<String, String> send(@RequestBody Map<String, String> body) {
        String to      = body.getOrDefault("to", "");
        String subject = body.getOrDefault("subject", "Уведомление");
        String text    = body.getOrDefault("text", "");

        if (to.isBlank()) {
            return Map.of("status", "error", "message", "Поле 'to' обязательно");
        }

        emailService.sendEmail(to, subject, text);
        return Map.of("status", "ok", "message", "Email поставлен в очередь отправки: " + to);
    }
}
