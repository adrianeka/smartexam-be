package com.tujuhsembilan.smartedutelu.domain.identity.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${application.mail.from}")
    private String fromAddress;

    @Value("${application.mail.password-reset-url}")
    private String passwordResetUrl;

    @Async
    @CircuitBreaker(name = "email", fallbackMethod = "sendPasswordResetEmailFallback")
    public void sendPasswordResetEmail(String toEmail, String token) {
        try {
            String resetLink = passwordResetUrl + "?token=" + token;

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(toEmail);
            message.setSubject("SmartEdu - Reset Password");
            message.setText(
                    "Halo,\n\n" +
                    "Kami menerima permintaan untuk reset password akun SmartEdu Anda.\n\n" +
                    "Klik link berikut untuk reset password:\n" +
                    resetLink + "\n\n" +
                    "Link ini berlaku selama 1 jam.\n\n" +
                    "Jika Anda tidak meminta reset password, abaikan email ini.\n\n" +
                    "Terima kasih,\nTim SmartEdu"
            );

            mailSender.send(message);
            log.debug("Password reset email sent to {}", toEmail);
        } catch (Exception e) {
            // B15: Log full error for monitoring/alerting — email failure is non-blocking
            // but must be visible in logs for operational troubleshooting
            log.error("Gagal mengirim email reset password ke {}: {} — {}",
                    toEmail, e.getClass().getSimpleName(), e.getMessage());
        }
    }

    // J8: Circuit breaker fallback — dipanggil saat mail server tidak dapat dijangkau
    private void sendPasswordResetEmailFallback(String toEmail, String token, Throwable t) {
        log.error("Circuit breaker aktif: email service tidak tersedia. Email ke {} tidak dikirim. Penyebab: {}",
                toEmail, t.getMessage());
    }
}
