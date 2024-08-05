package com.infinitynet.server.dtos.responses.authentication;

public record ForgotPasswordResponse(

        String message,

        String token

) {
}
