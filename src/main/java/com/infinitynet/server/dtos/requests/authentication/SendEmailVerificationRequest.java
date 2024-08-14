package com.infinitynet.server.dtos.requests.authentication;

import com.infinitynet.server.entities.Verification;
import com.infinitynet.server.enums.VerificationType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SendEmailVerificationRequest (

        @NotNull(message = "null_email")
        @NotBlank(message = "blank_email")
        @Email(message = "invalid_email")
        String email,

        @NotNull(message = "null_verification_type")
        VerificationType type

) {
}
