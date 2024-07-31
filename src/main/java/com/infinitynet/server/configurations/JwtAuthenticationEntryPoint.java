package com.infinitynet.server.configurations;

import java.io.IOException;

import com.infinitynet.server.dtos.responses.ApiResponse;
import com.infinitynet.server.exceptions.AuthenticationErrorCodes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.fasterxml.jackson.databind.ObjectMapper;

import static com.infinitynet.server.components.Translator.getLocalizedMessage;
import static com.infinitynet.server.exceptions.AuthenticationErrorCodes.TOKEN_INVALID;

// This class is used to handle the exception when the user is not authenticated
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        AuthenticationErrorCodes authenticationErrorCodes = TOKEN_INVALID;

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .errorCode(authenticationErrorCodes.getCode())
                .message(getLocalizedMessage(authenticationErrorCodes.getMessage()))
                .build();

        ObjectMapper objectMapper = new ObjectMapper();

        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        response.flushBuffer();
    }

}