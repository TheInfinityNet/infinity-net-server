package com.infinitynet.server.dtos.responses.authentication;

import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IntrospectResponse {

    boolean valid;

}