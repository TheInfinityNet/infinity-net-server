package com.infinitynet.server.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(101, "uncategorized", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_NOT_EXISTED(105, "user_not_found", HttpStatus.NOT_FOUND),
    USER_EXISTED(102, "user_existed", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(106, "unauthenticated", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN(107, "invalid_token", HttpStatus.UNAUTHORIZED),
    CANNOT_SEND_EMAIL(108, "cannot_send_email", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_NOT_ACTIVATED(109, "user_not_activated", HttpStatus.BAD_REQUEST),
    INVALID_ACTIVATION_CODE(110, "invalid_activation_code", HttpStatus.BAD_REQUEST)
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;

}