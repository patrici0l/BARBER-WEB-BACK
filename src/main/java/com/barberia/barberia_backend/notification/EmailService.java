package com.barberia.barberia_backend.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    public void sendEmail(String to, String subject, String body) {
        sendEmailWithResult(to, subject, body);
    }

    public DeliveryResult sendEmailWithResult(String to, String subject, String body) {
        if (!mailEnabled) {
            log.info("Email desactivado. No se envio correo a: {}", to);
            return DeliveryResult.skipped("Email desactivado por app.mail.enabled=false");
        }

        if (to == null || to.isBlank()) {
            log.warn("No se puede enviar email porque el destinatario esta vacio");
            return DeliveryResult.skipped("Destinatario de email vacio");
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            javaMailSender.send(message);

            log.info("Email enviado correctamente a: {}", to);
            return DeliveryResult.sent("Email enviado correctamente");
        } catch (Exception exception) {
            log.error("Error enviando email a {}: {}", to, exception.getMessage());
            return DeliveryResult.failed(exception.getMessage());
        }
    }
}
