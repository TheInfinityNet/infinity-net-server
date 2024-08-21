package com.infinitynet.server.exceptions;

import com.infinitynet.server.dtos.responses.CommonResponse;

import com.infinitynet.server.exceptions.authentication.AuthenticationErrorCode;
import com.infinitynet.server.exceptions.authentication.AuthenticationException;
import com.infinitynet.server.exceptions.file_storage.FileStorageErrorCode;
import com.infinitynet.server.exceptions.file_storage.FileStorageException;
import com.infinitynet.server.exceptions.post.PostErrorCode;
import com.infinitynet.server.exceptions.post.PostException;
import org.springframework.context.NoSuchMessageException;
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
import static com.infinitynet.server.exceptions.authentication.AuthenticationErrorCode.*;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Handle exceptions that are not caught by other handlers
    @ExceptionHandler(value = RuntimeException.class)
    ResponseEntity<CommonResponse<?>> handlingRuntimeException(RuntimeException exception) {
        log.error("Exception: ", exception);
        CommonResponse<?> commonResponse = new CommonResponse<>();

        commonResponse.setMessage(getLocalizedMessage("uncategorized"));

        return ResponseEntity.badRequest().body(commonResponse);
    }

    // Handle exceptions about messages that are not found
    @ExceptionHandler(value = NoSuchMessageException.class)
    ResponseEntity<CommonResponse<?>> handlingNoSuchMessageException(NoSuchMessageException exception) {
        log.error("Exception: ", exception);
        CommonResponse<?> commonResponse = new CommonResponse<>();

        commonResponse.setMessage(getLocalizedMessage("no_such_message"));

        return ResponseEntity.badRequest().body(commonResponse);
    }

    // Handle authentication exceptions
    @ExceptionHandler(value = AuthenticationException.class)
    ResponseEntity<CommonResponse<?>> handlingAuthenticationExceptions(AuthenticationException exception) {
        AuthenticationErrorCode authenticationErrorCode = exception.getAuthenticationErrorCode();
        CommonResponse<Map<String, String>> commonResponse = new CommonResponse<>();

        commonResponse.setErrorCode(authenticationErrorCode.getCode());
        commonResponse.setMessage(getLocalizedMessage(authenticationErrorCode.getMessage()));

        Map<String, String> errors = new HashMap<>();
        try{
            switch (authenticationErrorCode) {
                case VALIDATION_ERROR -> {
                    errors.put("email", getLocalizedMessage(EXPIRED_PASSWORD.getMessage()));
                    errors.put("password", getLocalizedMessage(EXPIRED_PASSWORD.getMessage()));
                }
                case EXPIRED_PASSWORD -> errors.put("password", getLocalizedMessage(EXPIRED_PASSWORD.getMessage()));

                case TOKEN_INVALID -> errors.put("token", getLocalizedMessage(TOKEN_INVALID.getMessage()));

                case WRONG_PASSWORD -> errors.put("password", getLocalizedMessage(WRONG_PASSWORD.getMessage()));

                case PASSWORD_MIS_MATCH -> errors.put("password", getLocalizedMessage(PASSWORD_MIS_MATCH.getMessage()));

                case EMAIL_ALREADY_IN_USE -> errors.put("email", getLocalizedMessage(EMAIL_ALREADY_IN_USE.getMessage()));

                case WEAK_PASSWORD -> errors.put("password", getLocalizedMessage(WEAK_PASSWORD.getMessage()));

                case INVALID_EMAIL -> errors.put("email", getLocalizedMessage(INVALID_EMAIL.getMessage()));

                case TERMS_NOT_ACCEPTED -> errors.put("termsAccepted", getLocalizedMessage(TERMS_NOT_ACCEPTED.getMessage()));

                case CODE_INVALID -> errors.put("code", getLocalizedMessage(CODE_INVALID.getMessage()));

                default -> errors = null;
            }

        } catch (NoSuchMessageException exception1) {
            CommonResponse<?> commonResponse1 = new CommonResponse<>();
            commonResponse1.setMessage(getLocalizedMessage("no_such_message"));
            return ResponseEntity.badRequest().body(commonResponse1);
        }

        commonResponse.setErrors(errors);

        return ResponseEntity.status(exception.getHttpStatus()).body(commonResponse);
    }

    // Handle file storage exceptions
    @ExceptionHandler(value = FileStorageException.class)
    ResponseEntity<CommonResponse<?>> handlingFileStorageExceptions(FileStorageException exception) {
        FileStorageErrorCode authenticationErrorCode = exception.getFileStorageErrorCode();
        CommonResponse<?> commonResponse = new CommonResponse<>();

        commonResponse.setErrorCode(authenticationErrorCode.getCode());
        commonResponse.setMessage(getLocalizedMessage(authenticationErrorCode.getMessage()));
        if (exception.getMoreInfo() != null) {
            commonResponse.setMessage(getLocalizedMessage(authenticationErrorCode.getMessage(), exception.getMoreInfo()));
        }

        return ResponseEntity.status(exception.getHttpStatus()).body(commonResponse);
    }

    // Handle post exceptions
    @ExceptionHandler(value = PostException.class)
    ResponseEntity<CommonResponse<?>> handlingPostExceptions(PostException exception) {
        PostErrorCode postErrorCode = exception.getPostErrorCode();
        CommonResponse<?> commonResponse = new CommonResponse<>();

        commonResponse.setErrorCode(postErrorCode.getCode());
        commonResponse.setMessage(getLocalizedMessage(postErrorCode.getMessage()));
        if (exception.getMoreInfo() != null) {
            commonResponse.setMessage(getLocalizedMessage(postErrorCode.getMessage(), exception.getMoreInfo()));
        }

        return ResponseEntity.status(exception.getHttpStatus()).body(commonResponse);
    }

    // Handle exceptions that request data is invalid (validation)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<?>>
    handleMethodArgumentNotValidExceptions(MethodArgumentNotValidException e) {
        try {
            Map<String, String> errors = new HashMap<>();
            e.getBindingResult().getAllErrors()
                    .forEach((error) -> {
                        String field = ((FieldError) error).getField();
                        String errorMessage = getLocalizedMessage(error.getDefaultMessage());
                        errors.put(field, errorMessage);
                    });

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    CommonResponse.builder()
                            .errorCode(VALIDATION_ERROR.getCode())
                            .message(getLocalizedMessage(VALIDATION_ERROR.getMessage()))
                            .errors(errors)
                            .build()
            );

        } catch (NoSuchMessageException exception) {
            CommonResponse<?> commonResponse = new CommonResponse<>();
            commonResponse.setMessage(getLocalizedMessage("no_such_message"));
            return ResponseEntity.badRequest().body(commonResponse);
        }
    }

}