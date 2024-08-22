package com.infinitynet.server.configurations.security_configs;

import java.io.IOException;

import com.infinitynet.server.dtos.responses.CommonResponse;
import com.infinitynet.server.exceptions.authentication.AuthenticationErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.fasterxml.jackson.databind.ObjectMapper;

import static com.infinitynet.server.components.Translator.getLocalizedMessage;
import static com.infinitynet.server.exceptions.authentication.AuthenticationErrorCode.TOKEN_MISSING;

// This class is used to handle the exception when the user is not authenticated
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        AuthenticationErrorCode authenticationErrorCode = TOKEN_MISSING;

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        CommonResponse<?> commonResponse = CommonResponse.builder()
                .errorCode(authenticationErrorCode.getCode())
                .message(getLocalizedMessage(authenticationErrorCode.getMessage()))
                .build();

        ObjectMapper objectMapper = new ObjectMapper();

        log.error("Unauthorized error: {}", authException.getMessage());
        response.getWriter().write(objectMapper.writeValueAsString(commonResponse));
        response.flushBuffer();
    }

}