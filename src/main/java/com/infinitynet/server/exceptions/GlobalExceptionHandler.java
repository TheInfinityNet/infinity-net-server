package com.infinitynet.server.exceptions;

import com.infinitynet.server.dtos.responses.ApiResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import static com.infinitynet.server.components.Translator.getLocalizedMessage;
import static com.infinitynet.server.exceptions.AuthenticationErrorCodes.*;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Handle exceptions that are not caught by other handlers
    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse<?>> handlingRuntimeException(RuntimeException exception) {
        log.error("Exception: ", exception);
        ApiResponse<?> apiResponse = new ApiResponse<>();

        apiResponse.setMessage(getLocalizedMessage("uncategorized"));

        return ResponseEntity.badRequest().body(apiResponse);
    }

    // Handle exceptions that are defined in the application
    @ExceptionHandler(value = AuthenticationExceptions.class)
    ResponseEntity<ApiResponse<?>> handlingAuthenticationExceptions(AuthenticationExceptions exception) {
        AuthenticationErrorCodes authenticationErrorCodes = exception.getAuthenticationErrorCodes();
        ApiResponse<Map<String, String>> apiResponse = new ApiResponse<>();

//        HttpStatus httpStatus = switch (authenticationErrorCodes) {
//            case PASSWORD_MIS_MATCH,
//                 WEAK_PASSWORD,
//                 INVALID_EMAIL,
//                 TERMS_NOT_ACCEPTED,
//                 INVALID_TOKEN -> HttpStatus.BAD_REQUEST;
//
//            case EXPIRED_PASSWORD,
//                 EMAIL_ALREADY_IN_USE -> HttpStatus.CONFLICT;
//
//            case TWO_FACTOR_REQUIRED,
//                 USER_DISABLED -> HttpStatus.FORBIDDEN;
//
//            case TOKEN_INVALID,
//                 WRONG_PASSWORD,
//                 TOKEN_EXPIRED,
//                 TOKEN_REVOKED,
//                 TOKEN_BLACKLISTED,
//                 INVALID_SIGNATURE -> HttpStatus.UNAUTHORIZED;
//
//            case USER_NOT_FOUND -> HttpStatus.NOT_FOUND;
//
//            case RATE_LIMIT_EXCEEDED -> HttpStatus.TOO_MANY_REQUESTS;
//
//            default -> HttpStatus.SERVICE_UNAVAILABLE;
//        };

        apiResponse.setErrorCode(authenticationErrorCodes.getCode());

        apiResponse.setMessage(getLocalizedMessage(authenticationErrorCodes.getMessage()));

        Map<String, String> errors = new HashMap<>();
        var result = switch (authenticationErrorCodes) {
            case VALIDATION_ERROR -> {
                errors.put("email", getLocalizedMessage(EXPIRED_PASSWORD.getMessage()));
                errors.put("password", getLocalizedMessage(EXPIRED_PASSWORD.getMessage()));
                yield errors;
            }
            case EXPIRED_PASSWORD -> {
                errors.put("password", getLocalizedMessage(EXPIRED_PASSWORD.getMessage()));
                yield errors;

            } case TOKEN_INVALID -> {
                errors.put("token", getLocalizedMessage(TOKEN_INVALID.getMessage()));
                yield errors;

            } case WRONG_PASSWORD -> {
                errors.put("password", getLocalizedMessage(WRONG_PASSWORD.getMessage()));
                yield errors;

            } case PASSWORD_MIS_MATCH -> {
                errors.put("password", getLocalizedMessage(PASSWORD_MIS_MATCH.getMessage()));
                yield errors;

            } case EMAIL_ALREADY_IN_USE -> {
                errors.put("email", getLocalizedMessage(EMAIL_ALREADY_IN_USE.getMessage()));
                yield errors;

            } case WEAK_PASSWORD -> {
                errors.put("password", getLocalizedMessage(WEAK_PASSWORD.getMessage()));
                yield errors;

            } case INVALID_EMAIL -> {
                errors.put("email", getLocalizedMessage(INVALID_EMAIL.getMessage()));
                yield errors;

            } case TERMS_NOT_ACCEPTED -> {
                errors.put("termsAccepted", getLocalizedMessage(TERMS_NOT_ACCEPTED.getMessage()));
                yield errors;
            }
            default -> null;
        };

        apiResponse.setErrors(result);

        return ResponseEntity.status(exception.getHttpStatus()).body(apiResponse);
    }

    // Handle exceptions that request data is invalid (validation)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>>
    handleMethodArgumentNotValidExceptions(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors()
                .forEach((error) -> {
                    String field = ((FieldError) error).getField();
                    String errorMessage = getLocalizedMessage(error.getDefaultMessage());
                    errors.put(field, errorMessage);
                });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ApiResponse<>(VALIDATION_ERROR.getCode(),
                        getLocalizedMessage(VALIDATION_ERROR.getMessage()), null, errors)
        );
    }

}