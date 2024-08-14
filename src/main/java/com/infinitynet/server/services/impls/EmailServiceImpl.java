package com.infinitynet.server.services.impls;

import com.infinitynet.server.dtos.others.MailActor;
import com.infinitynet.server.dtos.others.SendBrevoEmailDetails;
import com.infinitynet.server.dtos.requests.SendBrevoEmailRequest;
import com.infinitynet.server.dtos.responses.EmailResponse;
import com.infinitynet.server.entities.Verification;
import com.infinitynet.server.exceptions.authentication.AuthenticationException;
import com.infinitynet.server.repositories.VerificationRepository;
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
import static com.infinitynet.server.exceptions.authentication.AuthenticationErrorCode.TOKEN_EXPIRED;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EmailServiceImpl implements EmailService {

    BrevoEmailClient brevoEmailClient;

    VerificationRepository verificationRepository;

    @Value("${brevo-mail.from-mail}")
    @NonFinal
    String fromMail;

    @Value("${brevo-mail.api-key}")
    @NonFinal
    String apiKey;

    @Override
    public EmailResponse sendEmail(SendBrevoEmailDetails details, String token) {
        SendBrevoEmailRequest sendBrevoEmailRequest = null;
        switch (details.getType()) {
            case VERIFY_EMAIL_BY_CODE -> {
                Verification verification = verificationRepository.findByToken(token)
                        .orElseThrow(() -> new AuthenticationException(TOKEN_EXPIRED, HttpStatus.UNPROCESSABLE_ENTITY));

                sendBrevoEmailRequest = SendBrevoEmailRequest.builder()
                        .sender(new MailActor("Infinity Net Social Network", fromMail))
                        .to(details.getTo())
                        .subject(details.getSubject())
                        .htmlContent("<html><head></head><body><p>Hello, " + details.getTo().getFirst().email()
                                + "</p>This is the code to verify your account<br>" + verification.getCode() + ".</p></body></html>")
                        .build();

            } case VERIFY_EMAIL_BY_TOKEN -> {
                String verifyLink = "http://localhost:8080/infinity-net/api/v1/auth/verify-email-by-token?token=" + token;
                sendBrevoEmailRequest = SendBrevoEmailRequest.builder()
                        .sender(new MailActor("Infinity Net Social Network", fromMail))
                        .to(details.getTo())
                        .subject(details.getSubject())
                        .htmlContent("<html><head></head><body><p>Hello, " + details.getTo().getFirst().email()
                                + "</p>This is the link to verify your account<br>" + verifyLink + ".</p></body></html>")
                        .build();

            } case RESET_PASSWORD -> {
                Verification verification = verificationRepository.findByToken(token)
                        .orElseThrow(() -> new AuthenticationException(TOKEN_EXPIRED, HttpStatus.UNPROCESSABLE_ENTITY));

                sendBrevoEmailRequest = SendBrevoEmailRequest.builder()
                        .sender(new MailActor("Infinity Net Social Network", fromMail))
                        .to(details.getTo())
                        .subject(details.getSubject())
//                .templateId(details.getType())
//                .params(params)
                        .htmlContent("<html><head></head><body><p>Hello, " + details.getTo().getFirst().email()
                                + "</p>This is the code to reset your password<br>" + verification.getCode() + ".</p></body></html>")
                        .build();

            } default -> {
                log.error("Unknown email type: {}", details.getType());
                throw new AuthenticationException(CANNOT_SEND_EMAIL, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        };

        try {
            return brevoEmailClient.sendEmail(apiKey, sendBrevoEmailRequest);

        } catch (FeignException e) {
            log.error("Cannot send email", e);
            throw new AuthenticationException(CANNOT_SEND_EMAIL, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

}