package com.infinitynet.server.configurations.security_configs;

import java.io.IOException;

import com.infinitynet.server.dtos.responses.ApiResponse;
import com.infinitynet.server.exceptions.authentication.AuthenticationErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.fasterxml.jackson.databind.ObjectMapper;

import static com.infinitynet.server.components.Translator.getLocalizedMessage;
import static com.infinitynet.server.exceptions.authentication.AuthenticationErrorCode.TOKEN_MISSING;

// This class is used to handle the exception when the user is not authenticated
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        AuthenticationErrorCode authenticationErrorCode = TOKEN_MISSING;

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .errorCode(authenticationErrorCode.getCode())
                .message(getLocalizedMessage(authenticationErrorCode.getMessage()))
                .build();

        ObjectMapper objectMapper = new ObjectMapper();

        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        response.flushBuffer();
    }

}