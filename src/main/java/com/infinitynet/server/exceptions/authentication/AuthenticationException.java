package com.infinitynet.server.exceptions.authentication;

import com.infinitynet.server.exceptions.AppException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AuthenticationException extends AppException {

    public AuthenticationException(AuthenticationErrorCode authenticationErrorCode, HttpStatus httpStatus) {
        super(authenticationErrorCode.getMessage(), httpStatus);
        this.authenticationErrorCode = authenticationErrorCode;
    }

    private final AuthenticationErrorCode authenticationErrorCode;

}