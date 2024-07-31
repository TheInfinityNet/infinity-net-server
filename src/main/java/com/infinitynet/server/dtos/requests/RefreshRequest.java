package com.infinitynet.server.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RefreshRequest (

    @NotNull
    @NotBlank
    String refreshToken

) {
}