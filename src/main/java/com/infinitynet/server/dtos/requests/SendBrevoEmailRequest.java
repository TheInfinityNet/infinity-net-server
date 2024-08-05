package com.infinitynet.server.dtos.requests;

import com.infinitynet.server.dtos.others.MailActor;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SendBrevoEmailRequest {

    MailActor sender;

    List<MailActor> to;

    String subject;

    String htmlContent;

//    Integer templateId;
//
//    Map<String, String> params;
//
//    @Builder.Default
//    MailHeaders headers = new MailHeaders(DEFAULT_MAIL_HEADERS_MAILIN_CUSTOM, DEFAULT_MAIL_HEADERS_CHARSET);

}
