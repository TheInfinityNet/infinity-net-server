package com.infinitynet.server.dtos.requests;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationRequest {

    @NotNull(message = "invalid_email")
    @NotBlank(message = "invalid_email")
    String email;

    @NotNull(message = "invalid_password")
    @NotBlank(message = "invalid_password")
    String password;

}