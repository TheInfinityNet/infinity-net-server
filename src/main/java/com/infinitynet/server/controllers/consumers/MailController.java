package com.infinitynet.server.controllers.consumers;

import com.infinitynet.server.dtos.others.MailActor;
import com.infinitynet.server.dtos.others.SendEmailDetails;
import com.infinitynet.server.dtos.responses.EmailResponse;
import com.infinitynet.server.services.EmailService;
import com.infinitynet.server.services.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.infinitynet.server.constants.Constants.KAFKA_TOPIC_SEND_MAIL;
import static com.infinitynet.server.constants.Constants.SEND_MAIL_TO_NEW_USER;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class MailController {

    EmailService emailService;

    UserService userService;

    @KafkaListener(topics = KAFKA_TOPIC_SEND_MAIL)
    public void listenNotificationDelivery(String message) {
        String type = message.split(":")[0];

        switch (type) {
            case "new-user":
                String id = message.split(":")[1];
                String email = userService.getById(id).getEmail();
                String activationCode = message.split(":")[2];

                String verifyLink =
                        "http://localhost:8080/infinity-net/api/v1/auth/activate/" + id + "/" + activationCode;
                //log.info("Email: {}", email);

                EmailResponse response = emailService.sendActivateEmail(SendEmailDetails.builder()
                        .to(List.of(new MailActor("User", email)))
                        .subject("Welcome to Infinity Net")
                        .type(SEND_MAIL_TO_NEW_USER)
                        .build(), verifyLink);

                log.info("Email sent: {}", response);
                break;
            default:
                log.error("Unknown message type: {}", type);
        }

        //log.info("Message received: {}", message);
    }

}
