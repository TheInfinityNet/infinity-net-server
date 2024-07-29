package com.infinitynet.server.services;

import com.infinitynet.server.dtos.others.MailActor;
import com.infinitynet.server.dtos.others.SendEmailDetails;
import com.infinitynet.server.dtos.requests.EmailRequest;
import com.infinitynet.server.dtos.responses.EmailResponse;
import com.infinitynet.server.exceptions.AppException;
import com.infinitynet.server.repositories.httpclients.EmailClient;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.infinitynet.server.constants.Constants.SEND_MAIL_TO_NEW_USER;
import static com.infinitynet.server.exceptions.ErrorCode.CANNOT_SEND_EMAIL;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EmailService {

    EmailClient emailClient;

    @Value("${brevo-mail.from-mail}")
    @NonFinal
    String fromMail;

    @Value("${brevo-mail.api-key}")
    @NonFinal
    String apiKey;

    public EmailResponse sendActivateEmail(SendEmailDetails details, String verifyLink) {

//        Map<String, String> params = new HashMap<>();
//
//        switch (details.getType()) {
//            case SEND_MAIL_TO_NEW_USER:
//                params.put("verify-link", "abc.com");
//                break;
//            case 2:
//                params.put("reset-link", "abc.com");
//                break;
//            default:
//                log.error("Unknown email type: {}", details.getType());
//                throw new AppException(CANNOT_SEND_EMAIL);
//        }

        EmailRequest emailRequest = EmailRequest.builder()
                .sender(new MailActor("Infinity Net Social Network", fromMail))
                .to(details.getTo())
                .subject(details.getSubject())
//                .templateId(details.getType())
//                .params(params)
                .htmlContent("<html><head></head><body><p>Hello, "+ details.getTo().getFirst().email()
                        +"</p>This is thi link to verify your account<br>"+ verifyLink +".</p></body></html>")
                .build();
        try {
            return emailClient.sendEmail(apiKey, emailRequest);

        } catch (FeignException e){
            log.error("Cannot send email", e);
            throw new AppException(CANNOT_SEND_EMAIL);
        }
    }

}