package com.infinitynet.server.dtos.requests;

import jakarta.validation.constraints.*;

public record SignInRequest(

    @NotNull(message = "invalid_email")
    @NotBlank(message = "invalid_email")
    String email,

    @NotNull(message = "invalid_password")
    @NotBlank(message = "invalid_password")
    String password

) {
}