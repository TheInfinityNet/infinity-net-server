package com.infinitynet.server.dtos.requests.authentication;

import com.infinitynet.server.dtos.validates.Adult;
import com.infinitynet.server.dtos.validates.ValidPhoneNumber;
import com.infinitynet.server.enums.Gender;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record SignUpRequest(

        @NotNull(message = "null_name")
        @NotBlank(message = "blank_name")
        String firstName,

        @NotNull(message = "null_name")
        @NotBlank(message = "blank_name")
        String lastName,

        String middleName,

        @NotNull(message = "null_name")
        @NotBlank(message = "blank_name")
        String userName,

        @NotNull(message = "null_email")
        @NotBlank(message = "blank_email")
        @Email(message = "invalid_email")
        String email,

        @NotNull(message = "null_password")
        @NotBlank(message = "blank_password")
        @Size(min = 6, max = 20, message = "size_password")
        String password,

        @NotNull(message = "null_password")
        @NotBlank(message = "blank_password")
        @Size(min = 6, max = 20, message = "size_password")
        String passwordConfirmation,

        @NotBlank(message = "blank_phone_number")
        @NotNull(message = "null_phone_number")
        @ValidPhoneNumber(message = "invalid_phone_number")
        String mobileNumber,

        @NotNull(message = "null_accept_terms")
        @Adult
        LocalDate birthdate,

        @NotNull(message = "null_accept_terms")
        Gender gender,

        @NotNull(message = "null_accept_terms")
        Boolean acceptTerms
) {

}