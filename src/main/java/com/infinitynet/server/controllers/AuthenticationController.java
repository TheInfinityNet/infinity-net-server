package com.infinitynet.server.controllers;

import java.text.ParseException;

import com.infinitynet.server.dtos.requests.*;
import com.infinitynet.server.dtos.responses.ApiResponse;
import com.infinitynet.server.dtos.responses.AuthenticationResponse;
import com.infinitynet.server.dtos.responses.IntrospectResponse;
import com.infinitynet.server.dtos.responses.UserResponse;
import com.infinitynet.server.services.AuthenticationService;
import com.infinitynet.server.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    UserService userService;

    @Operation(summary = "Sign up", description = "Create new user")
    @PostMapping("/sign-up")
    ResponseEntity<ApiResponse<UserResponse>> signUp(@RequestBody @Valid UserCreationRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.<UserResponse>builder().result(userService.createUser(request)).build());
    }

    @Operation(summary = "Activate", description = "Activate user")
    @GetMapping("/activate/{id}/{code}")
    ResponseEntity<ApiResponse<AuthenticationResponse>> activate(@PathVariable String id, @PathVariable String code) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.<AuthenticationResponse>builder()
                        .result(authenticationService.activate(id, code)).build());
    }


    @Operation(summary = "Sign in", description = "Authenticate user and return token")
    @PostMapping("/sign-in")
    ResponseEntity<ApiResponse<AuthenticationResponse>> signIn(@RequestBody AuthenticationRequest request) {
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