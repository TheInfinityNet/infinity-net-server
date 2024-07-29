package com.infinitynet.server.dtos.requests;

import com.infinitynet.server.dtos.others.MailActor;
import com.infinitynet.server.dtos.others.MailHeaders;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Map;

import static com.infinitynet.server.constants.Constants.DEFAULT_MAIL_HEADERS_CHARSET;
import static com.infinitynet.server.constants.Constants.DEFAULT_MAIL_HEADERS_MAILIN_CUSTOM;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailRequest {

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
