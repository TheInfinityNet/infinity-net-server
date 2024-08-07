package com.infinitynet.server.exceptions.authentication;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AuthenticationException extends RuntimeException {

    public AuthenticationException(AuthenticationErrorCode authenticationErrorCode, HttpStatus httpStatus) {
        super(authenticationErrorCode.getMessage());
        this.authenticationErrorCode = authenticationErrorCode;
        this.httpStatus = httpStatus;
    }

    private final AuthenticationErrorCode authenticationErrorCode;
    private final HttpStatus httpStatus;

}