package com.infinitynet.server.controllers.consumers;

import com.infinitynet.server.dtos.others.MailActor;
import com.infinitynet.server.dtos.others.SendBrevoEmailDetails;
import com.infinitynet.server.dtos.responses.EmailResponse;
import com.infinitynet.server.enums.VerificationType;
import com.infinitynet.server.services.EmailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.infinitynet.server.Constants.KAFKA_TOPIC_SEND_MAIL;
import static com.infinitynet.server.enums.VerificationType.VERIFY_EMAIL_BY_CODE;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EmailController {

    EmailService emailService;

    @KafkaListener(topics = KAFKA_TOPIC_SEND_MAIL)
    public void listenNotificationDelivery(String message) {
        String type = message.split(":")[0];
        String email = message.split(":")[1];
        String token = message.split(":")[2];

        log.info("Message received: {}", message);
        //log.info("Email: {}", email);
        EmailResponse response = switch (VerificationType.valueOf(type)) {
            case VERIFY_EMAIL_BY_CODE,
                 VERIFY_EMAIL_BY_TOKEN ->
                emailService.sendEmail(SendBrevoEmailDetails.builder()
                        .to(List.of(new MailActor("User", email)))
                        .subject("Welcome to Infinity Net")
                        .type(VerificationType.valueOf(type))
                        .build(), token);

            case RESET_PASSWORD ->
                emailService.sendEmail(SendBrevoEmailDetails.builder()
                        .to(List.of(new MailActor("User", email)))
                        .subject("Reset Password")
                        .type(VERIFY_EMAIL_BY_CODE)
                        .build(), token);
        };
        log.info("Email sent: {}", response);
    }

}
