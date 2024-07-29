package com.infinitynet.server.repositories.httpclients;

import com.infinitynet.server.dtos.requests.EmailRequest;
import com.infinitynet.server.dtos.responses.EmailResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "brevo-mail-client", url = "${brevo-mail.url}")
public interface EmailClient {

    @PostMapping(value = "/v3/smtp/email", produces = MediaType.APPLICATION_JSON_VALUE)
    EmailResponse sendEmail(@RequestHeader("api-key") String apiKey, @RequestBody EmailRequest body);

}