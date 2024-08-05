package com.infinitynet.server.dtos.requests.authentication;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RefreshRequest (

    @NotNull(message = "null_token")
    @NotBlank(message = "blank_token")
    String refreshToken

) {
}