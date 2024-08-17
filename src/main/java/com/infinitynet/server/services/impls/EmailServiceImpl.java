package com.infinitynet.server.services.impls;

import com.infinitynet.server.dtos.others.MailActor;
import com.infinitynet.server.dtos.others.SendBrevoEmailDetails;
import com.infinitynet.server.dtos.requests.SendBrevoEmailRequest;
import com.infinitynet.server.exceptions.authentication.AuthenticationException;
import com.infinitynet.server.repositories.httpclients.BrevoEmailClient;
import com.infinitynet.server.services.EmailService;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static com.infinitynet.server.exceptions.authentication.AuthenticationErrorCode.CANNOT_SEND_EMAIL;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EmailServiceImpl implements EmailService {

    BrevoEmailClient brevoEmailClient;

    @Value("${brevo-mail.from-mail}")
    @NonFinal
    String fromMail;

    @Value("${brevo-mail.api-key}")
    @NonFinal
    String apiKey;

    @Override
    public String sendEmail(SendBrevoEmailDetails details, String token, String code) {
        String htmlContent =
        switch (details.getType()) {
            case VERIFY_EMAIL_BY_CODE ->
                "<html><head></head><body><p>Hello, " + details.getTo().getFirst().email()
                                + "</p>This is the code to verify your account<br>" + code + ".</p></body></html>";

            case VERIFY_EMAIL_BY_TOKEN -> "<html><head></head><body><p>Hello, " + details.getTo().getFirst().email()
                    + "</p>This is the link to verify your account<br>"
                    + "http://localhost:8080/infinity-net/api/v1/auth/verify-email-by-token?token=" + token + ".</p></body></html>";

            case RESET_PASSWORD -> "<html><head></head><body><p>Hello, " + details.getTo().getFirst().email()
                                + "</p>This is the code to reset your password<br>" + code + ".</p></body></html>";
        };

        SendBrevoEmailRequest sendBrevoEmailRequest = SendBrevoEmailRequest.builder()
                .sender(new MailActor("Infinity Net Social Network", fromMail))
                .to(details.getTo())
                .subject(details.getSubject())
                .htmlContent(htmlContent)
                .build();

        try {
            return brevoEmailClient.sendEmail(apiKey, sendBrevoEmailRequest).messageId();

        } catch (FeignException e) {
            log.error("Cannot send email", e);
            throw new AuthenticationException(CANNOT_SEND_EMAIL, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

}