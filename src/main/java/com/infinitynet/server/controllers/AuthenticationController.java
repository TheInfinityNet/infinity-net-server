package com.infinitynet.server.controllers;

import java.text.ParseException;

import com.infinitynet.server.annotations.RateLimit;
import com.infinitynet.server.dtos.requests.authentication.*;
import com.infinitynet.server.dtos.responses.*;
import com.infinitynet.server.dtos.responses.authentication.IntrospectResponse;
import com.infinitynet.server.enums.LimitKeyType;
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
    ResponseEntity<?> signUp(@RequestBody @Valid SignUpRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authenticationService.signUp(request));
    }

    @Operation(summary = "Verify email by code", description = "Verify email by code")
    @PostMapping("/verify-email-by-code")
    ResponseEntity<?> verifyEmail(@RequestBody @Valid VerifyEmailByCodeRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.verifyEmail(request, null));
    }

    @Operation(summary = "Verify email by token", description = "Verify email by token")
    @GetMapping("/verify-email-by-token")
    ResponseEntity<?> verifyEmail(@RequestParam(name = "token") String token) {
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.verifyEmail(null, token));
    }

    @Operation(summary = "Send email verification", description = "Send email verification")
    @PostMapping("/send-email-verification")
    ResponseEntity<?> sendEmailVerification(@RequestBody @Valid SendEmailVerificationRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.sendEmailVerification(request));
    }

    @Operation(summary = "Sign in", description = "Authenticate user and return token")
    @PostMapping("/sign-in")
    @RateLimit(limitKeyType = LimitKeyType.BY_IP)
    ResponseEntity<?> signIn(@RequestBody @Valid SignInRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.signIn(request));
    }

    @Operation(summary = "Sign out", description = "Sign out user")
    @PostMapping("/sign-out")
    void signOut(@RequestBody @Valid SignOutRequest request) throws ParseException, JOSEException {
        authenticationService.signOut(request);
    }

    @Operation(summary = "Refresh", description = "Refresh token")
    @PostMapping("/refresh")
    @RateLimit(limitKeyType = LimitKeyType.BY_TOKEN)
    ResponseEntity<?> refresh(@RequestBody @Valid RefreshRequest request,
                              HttpServletRequest httpServletRequest) throws ParseException, JOSEException {
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.refresh(request, httpServletRequest));
    }

    @Operation(summary = "Send email forgot password", description = "Send email forgot password")
    @PostMapping("/send-forgot-password")
    ResponseEntity<?> sendEmailForgotPassword(@RequestBody @Valid SendEmailForgotPasswordRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.sendEmailForgotPassword(request));
    }

    @Operation(summary = "Verify forgot password code", description = "Verify forgot password code")
    @PostMapping("/forgot-password")
    ResponseEntity<?> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.forgotPassword(request));
    }

    @Operation(summary = "Reset password", description = "Reset password")
    @PostMapping("/reset-password")
    ResponseEntity<?> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.resetPassword(request));
    }

    @Operation(summary = "Introspect", description = "Introspect provided token")
    @PostMapping("/introspect")
    ResponseEntity<ApiResponse<?>> introspect(@RequestBody @Valid IntrospectRequest request)
            throws ParseException, JOSEException {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.<IntrospectResponse>builder().results(authenticationService.introspect(request)).build());
    }

}