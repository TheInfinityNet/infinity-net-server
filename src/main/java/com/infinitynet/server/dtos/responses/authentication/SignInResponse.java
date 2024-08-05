package com.infinitynet.server.dtos.responses.authentication;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.infinitynet.server.dtos.others.Tokens;
import com.infinitynet.server.dtos.responses.UserInfoResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignInResponse {

    Tokens tokens;

    UserInfoResponse user;

}