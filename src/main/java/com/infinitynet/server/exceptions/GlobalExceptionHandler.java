package com.infinitynet.server.exceptions;

import com.infinitynet.server.dtos.responses.ApiResponse;

import com.infinitynet.server.dtos.responses.ValidateResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static com.infinitynet.server.components.Translator.getLocalizedMessage;
import static com.infinitynet.server.exceptions.ErrorCode.UNCATEGORIZED_EXCEPTION;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Handle exceptions that are not caught by other handlers
    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse<?>> handlingRuntimeException(RuntimeException exception) {
        log.error("Exception: ", exception);
        ApiResponse<?> apiResponse = new ApiResponse<>();

        apiResponse.setCode(UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(getLocalizedMessage(UNCATEGORIZED_EXCEPTION.getMessage()));

        return ResponseEntity.badRequest().body(apiResponse);
    }

    // Handle exceptions that are defined in the application
    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<?>> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ApiResponse<?> apiResponse = new ApiResponse<>();

        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(getLocalizedMessage(errorCode.getMessage()));

        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    // Handle exceptions that request data is invalid (validation)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<List<ValidateResponse>>>
    handleMethodArgumentNotValidExceptions(MethodArgumentNotValidException e) {
        List<ValidateResponse> errors = new ArrayList<>();
        e.getBindingResult().getAllErrors()
                .forEach((error) -> {
                    String field = ((FieldError) error).getField();
                    String errorMessage = getLocalizedMessage(error.getDefaultMessage());
                    errors.add(new ValidateResponse(field, errorMessage));
                });

        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                new ApiResponse<>(HttpStatus.NOT_ACCEPTABLE.value(),
                        getLocalizedMessage("invalid_request"), errors)
        );
    }

}