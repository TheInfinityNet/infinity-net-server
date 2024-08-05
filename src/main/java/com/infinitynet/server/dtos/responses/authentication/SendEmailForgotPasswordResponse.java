package com.infinitynet.server.dtos.responses.authentication;

import java.util.Date;

public record SendEmailForgotPasswordResponse(

    String message,

    Date retryAfter

) {

}