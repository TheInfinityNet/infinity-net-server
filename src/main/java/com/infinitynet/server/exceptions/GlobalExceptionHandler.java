package com.infinitynet.server.exceptions;

import com.infinitynet.server.dtos.responses.CommonResponse;

import com.infinitynet.server.exceptions.authentication.AuthenticationException;
import com.infinitynet.server.exceptions.file_storage.FileStorageException;
import com.infinitynet.server.exceptions.post.PostException;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import static com.infinitynet.server.Utils.getMessageForValidationException;
import static com.infinitynet.server.components.Translator.getLocalizedMessage;
import static com.infinitynet.server.exceptions.authentication.AuthenticationErrorCode.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Handle exceptions that are not caught by other handlers
    @ExceptionHandler(value = RuntimeException.class)
    ResponseEntity<CommonResponse<?>> handlingRuntimeException(RuntimeException exception) {
        log.error("Exception: ", exception);
        return ResponseEntity.badRequest().body(CommonResponse.builder()
                .message(getLocalizedMessage("uncategorized"))
                .build());
    }

    // Handle exceptions about messages that are not found
    @ExceptionHandler(value = NoSuchMessageException.class)
    ResponseEntity<CommonResponse<?>> handlingNoSuchMessageException(NoSuchMessageException exception) {
        log.error("Message Not Found Exception: ", exception);
        return ResponseEntity.status(BAD_REQUEST).body(CommonResponse.builder()
                .message(exception.getMessage())
                .build());
    }

    // Handle authentication exceptions
    @ExceptionHandler(value = AuthenticationException.class)
    ResponseEntity<CommonResponse<?>> handlingAuthenticationExceptions(AuthenticationException exception) {
        log.error("Authentication Exception: {}", exception.toString());
        return ResponseEntity.status(exception.getHttpStatus()).body(CommonResponse.builder()
                .errorCode(exception.getAuthenticationErrorCode().getCode())
                .message((exception.getMoreInfo() != null)
                        ? getMessageForValidationException(exception.getAuthenticationErrorCode().getMessage(), exception.getMoreInfo())
                        : getMessageForValidationException(exception.getAuthenticationErrorCode().getMessage()))
                .errors(switch (exception.getAuthenticationErrorCode()) {
                    case VALIDATION_ERROR -> new HashMap<>(Map.of(
                            "email", getMessageForValidationException(VALIDATION_ERROR.getMessage()),
                            "password", getMessageForValidationException(VALIDATION_ERROR.getMessage())));

                    case EXPIRED_PASSWORD ->
                            new HashMap<>(Map.of("password", getMessageForValidationException(EXPIRED_PASSWORD.getMessage())));

                    case TOKEN_INVALID ->
                            new HashMap<>(Map.of("token", getMessageForValidationException(TOKEN_INVALID.getMessage())));

                    case WRONG_PASSWORD ->
                            new HashMap<>(Map.of("password", getMessageForValidationException(WRONG_PASSWORD.getMessage())));

                    case PASSWORD_MIS_MATCH ->
                            new HashMap<>(Map.of("password", getMessageForValidationException(PASSWORD_MIS_MATCH.getMessage())));

                    case EMAIL_ALREADY_IN_USE ->
                            new HashMap<>(Map.of("email", getMessageForValidationException(EMAIL_ALREADY_IN_USE.getMessage())));

                    case WEAK_PASSWORD ->
                            new HashMap<>(Map.of("password", getMessageForValidationException(WEAK_PASSWORD.getMessage())));

                    case INVALID_EMAIL ->
                            new HashMap<>(Map.of("email", getMessageForValidationException(INVALID_EMAIL.getMessage())));

                    case TERMS_NOT_ACCEPTED ->
                            new HashMap<>(Map.of("termsAccepted", getMessageForValidationException(TERMS_NOT_ACCEPTED.getMessage())));

                    case CODE_INVALID ->
                            new HashMap<>(Map.of("code", getMessageForValidationException(CODE_INVALID.getMessage())));

                    default -> null;
                })
                .build());
    }

    // Handle file storage exceptions
    @ExceptionHandler(value = FileStorageException.class)
    ResponseEntity<CommonResponse<?>> handlingFileStorageExceptions(FileStorageException exception) {
        log.error("File Storage Exception: {}", exception.toString());
        return ResponseEntity.status(exception.getHttpStatus()).body(CommonResponse.builder()
                .errorCode(exception.getFileStorageErrorCode().getCode())
                .message((exception.getMoreInfo() != null)
                        ? getLocalizedMessage(exception.getFileStorageErrorCode().getMessage(), exception.getMoreInfo())
                        : getLocalizedMessage(exception.getFileStorageErrorCode().getMessage()))
                .build());
    }

    // Handle post exceptions
    @ExceptionHandler(value = PostException.class)
    ResponseEntity<CommonResponse<?>> handlingPostExceptions(PostException exception) {
        log.error("Post Exception: {}", exception.toString());
        return ResponseEntity.status(exception.getHttpStatus()).body(CommonResponse.builder()
                .errorCode(exception.getPostErrorCode().getCode())
                .message((exception.getMoreInfo() != null)
                        ? getLocalizedMessage(exception.getPostErrorCode().getMessage(), exception.getMoreInfo())
                        : getLocalizedMessage(exception.getPostErrorCode().getMessage()))
                .build());
    }

    // Handle exceptions that request data is invalid (validation)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<?>>
    handleMethodArgumentNotValidExceptions(MethodArgumentNotValidException e) {
        log.error("Validation Exception: ", e);
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors()
                .forEach((error) -> {
                    String field = ((FieldError) error).getField();
                    errors.put(field, getMessageForValidationException(error.getDefaultMessage()));
                });

        return ResponseEntity.status(BAD_REQUEST).body(
                CommonResponse.builder()
                        .errorCode(VALIDATION_ERROR.getCode())
                        .message(getLocalizedMessage(VALIDATION_ERROR.getMessage()))
                        .errors(errors)
                        .build()
        );
    }

}