package com.barberia.barberia_backend.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WhatsappService {

    @Value("${app.whatsapp.enabled:false}")
    private boolean whatsappEnabled;

    public DeliveryResult sendWhatsapp(String to, String message) {
        if (to == null || to.isBlank()) {
            return DeliveryResult.skipped("Destinatario de WhatsApp vacio");
        }

        if (!whatsappEnabled) {
            log.info("WhatsApp desactivado. Mensaje simulado para {}: {}", to, message);
            return DeliveryResult.skipped("WhatsApp desactivado. Configura un proveedor real para envio automatico.");
        }

        log.info("WhatsApp listo para proveedor externo. Destino: {}. Mensaje: {}", to, message);
        return DeliveryResult.skipped("Proveedor de WhatsApp no configurado en este proyecto");
    }
}
