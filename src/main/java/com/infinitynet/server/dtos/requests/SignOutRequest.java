package com.infinitynet.server.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SignOutRequest (

    @NotNull
    @NotBlank
    String accessToken,

    @NotNull
    @NotBlank
    String refreshToken

) {
}