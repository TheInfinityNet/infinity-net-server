package com.infinitynet.server.dtos.requests;

import jakarta.validation.constraints.*;

public record UserCreationRequest (

    @NotNull(message = "invalid_email")
    @NotBlank(message = "invalid_email")
    @Email(message = "invalid_email")
    String email,

    @NotNull(message = "invalid_password")
    @NotBlank(message = "invalid_password")
    @Size(min = 6, max = 20, message = "invalid_password")
    String password,

    @NotNull(message = "invalid_password")
    @NotBlank(message = "invalid_password")
    @Size(min = 6, max = 20, message = "invalid_password")
    String passwordConfirmation

) {

}