package com.infinitynet.server.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AuthenticationExceptions extends RuntimeException {

    public AuthenticationExceptions(AuthenticationErrorCodes authenticationErrorCodes, HttpStatus httpStatus) {
        super(authenticationErrorCodes.getMessage());
        this.authenticationErrorCodes = authenticationErrorCodes;
        this.httpStatus = httpStatus;
    }

    private final AuthenticationErrorCodes authenticationErrorCodes;
    private final HttpStatus httpStatus;

}