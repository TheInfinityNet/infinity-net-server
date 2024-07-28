package com.infinitynet.server.controllers;

import java.text.ParseException;

import com.infinitynet.server.dtos.requests.AuthenticationRequest;
import com.infinitynet.server.dtos.requests.IntrospectRequest;
import com.infinitynet.server.dtos.requests.LogoutRequest;
import com.infinitynet.server.dtos.requests.RefreshRequest;
import com.infinitynet.server.dtos.responses.ApiResponse;
import com.infinitynet.server.dtos.responses.AuthenticationResponse;
import com.infinitynet.server.dtos.responses.IntrospectResponse;
import com.infinitynet.server.services.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jose.JOSEException;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("${api.prefix}/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "Authentication APIs")
public class AuthenticationController {

    AuthenticationService authenticationService;

    @Operation(summary = "Authenticate", description = "Authenticate user and return token")
    @PostMapping("/token")
    ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(@RequestBody AuthenticationRequest request) {
        AuthenticationResponse result = authenticationService.authenticate(request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.<AuthenticationResponse>builder().result(result).build());
    }

    @Operation(summary = "Introspect", description = "Introspect provided token")
    @PostMapping("/introspect")
    ResponseEntity<ApiResponse<IntrospectResponse>> authenticate(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        IntrospectResponse result = authenticationService.introspect(request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.<IntrospectResponse>builder().result(result).build());
    }

    @Operation(summary = "Refresh", description = "Refresh token")
    @PostMapping("/refresh")
    ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(@RequestBody RefreshRequest request)
            throws ParseException, JOSEException {
        AuthenticationResponse result = authenticationService.refreshToken(request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.<AuthenticationResponse>builder().result(result).build());
    }

    @Operation(summary = "Logout", description = "Logout user")
    @PostMapping("/logout")
    ResponseEntity<ApiResponse<Void>> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<Void>builder().build());
    }

}