package com.infinitynet.server.dtos.others;

import com.infinitynet.server.entities.Verification;
import com.infinitynet.server.enums.VerificationType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SendBrevoEmailDetails {

    List<MailActor> to;

    String subject;

    VerificationType type;

}