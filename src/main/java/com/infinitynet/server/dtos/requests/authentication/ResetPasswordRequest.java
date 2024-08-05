package com.infinitynet.server.dtos.requests.authentication;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest (

    @NotNull(message = "null_token")
    @NotBlank(message = "blank_token")
    String token,

    @NotNull(message = "null_password")
    @NotBlank(message = "blank_password")
    @Size(min = 6, max = 20, message = "size_password")
    String password,

    @NotNull(message = "null_password")
    @NotBlank(message = "blank_password")
    @Size(min = 6, max = 20, message = "size_password")
    String passwordConfirmation

) {

}