package com.infinitynet.server.controllers;

import java.text.ParseException;

import com.infinitynet.server.dtos.requests.*;
import com.infinitynet.server.dtos.responses.*;
import com.infinitynet.server.services.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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

    @Operation(summary = "Sign up", description = "Create new user")
    @PostMapping("/sign-up")
    ResponseEntity<ApiResponse<UserResponse>> signUp(@RequestBody @Valid UserCreationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<UserResponse>builder().results(authenticationService.signUp(request)).build());
    }

    @Operation(summary = "Activate", description = "Activate user")
    @GetMapping("/activate/{id}/{code}")
    ResponseEntity<ApiResponse<ActivationResponse>> activate(@PathVariable String id, @PathVariable String code) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.<ActivationResponse>builder()
                        .results(authenticationService.activate(id, code)).build());
    }

    @Operation(summary = "Sign in", description = "Authenticate user and return token")
    @PostMapping("/sign-in")
    ResponseEntity<ApiResponse<SignInResponse>> signIn(@RequestBody SignInRequest request) {
        SignInResponse result = authenticationService.signIn(request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.<SignInResponse>builder().results(result).build());
    }

    @Operation(summary = "Introspect", description = "Introspect provided token")
    @PostMapping("/introspect")
    ResponseEntity<ApiResponse<IntrospectResponse>> introspect(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        IntrospectResponse result = authenticationService.introspect(request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.<IntrospectResponse>builder().results(result).build());
    }

    @Operation(summary = "Refresh", description = "Refresh token")
    @PostMapping("/refresh")
    ResponseEntity<ApiResponse<RefreshResponse>> refresh(@RequestBody RefreshRequest request,
                                                             HttpServletRequest httpServletRequest)
            throws ParseException, JOSEException {
        RefreshResponse result = authenticationService.refreshToken(request, httpServletRequest);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.<RefreshResponse>builder().results(result).build());
    }

    @Operation(summary = "Sign out", description = "Sign out user")
    @PostMapping("/sign-out")
    ResponseEntity<ApiResponse<Void>> signOut(@RequestBody SignOutRequest request) throws ParseException, JOSEException {
        authenticationService.signOut(request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.<Void>builder().build());
    }

}