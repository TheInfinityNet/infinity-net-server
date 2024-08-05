package com.infinitynet.server.dtos.requests.authentication;

import jakarta.validation.constraints.*;

public record SignInRequest (

    @NotNull(message = "null_email")
    @NotBlank(message = "blank_email")
    @Email(message = "invalid_email")
    String email,

    @NotNull(message = "null_password")
    @NotBlank(message = "blank_password")
    @Size(min = 6, max = 20, message = "size_password")
    String password

) {
}