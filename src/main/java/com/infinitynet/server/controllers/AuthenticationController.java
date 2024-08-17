package com.infinitynet.server.controllers;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import com.infinitynet.server.annotations.RateLimit;
import com.infinitynet.server.dtos.others.Tokens;
import com.infinitynet.server.dtos.requests.authentication.*;
import com.infinitynet.server.dtos.responses.authentication.*;
import com.infinitynet.server.entities.User;
import com.infinitynet.server.mappers.UserMapper;
import com.infinitynet.server.services.AuthenticationService;
import com.infinitynet.server.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nimbusds.jose.JOSEException;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import static com.infinitynet.server.components.Translator.getLocalizedMessage;
import static com.infinitynet.server.enums.RateLimitKeyType.BY_IP;
import static com.infinitynet.server.enums.RateLimitKeyType.BY_TOKEN;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("${api.prefix}/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "Authentication APIs")
public class AuthenticationController {

    AuthenticationService authenticationService;

    UserService userService;

    UserMapper userMapper = UserMapper.INSTANCE;

    @Operation(summary = "Sign up", description = "Create new user")
    @PostMapping("/sign-up")
    ResponseEntity<SignUpResponse> signUp(@RequestBody @Valid SignUpRequest request) {
        User user = userMapper.toUser(request);

        authenticationService.signUp(user, request.passwordConfirmation());

        return ResponseEntity.status(CREATED).body(
                new SignUpResponse(getLocalizedMessage("sign_up_success")));
    }

    @Operation(summary = "Send email verification", description = "Send email verification")
    @PostMapping("/send-email-verification")
    ResponseEntity<SendEmailVerificationResponse> sendEmailVerification(@RequestBody @Valid SendEmailVerificationRequest request) {
        authenticationService.sendEmailVerification(request.email(), request.type());

        return ResponseEntity.status(OK).body(
                new SendEmailVerificationResponse(getLocalizedMessage("resend_verification_email_success")));
    }

    @Operation(summary = "Verify email by code", description = "Verify email by code")
    @PostMapping("/verify-email-by-code")
    ResponseEntity<VerifyEmailResponse> verifyEmail(@RequestBody @Valid VerifyEmailByCodeRequest request) {
        User user = userService.findByEmail(request.email());

        authenticationService.verifyEmail(user, request.code(), null);

        return ResponseEntity.status(OK).body(
                new VerifyEmailResponse(getLocalizedMessage("verify_email_success")));
    }

    @Operation(summary = "Verify email by token", description = "Verify email by token")
    @GetMapping("/verify-email-by-token")
    ResponseEntity<VerifyEmailResponse> verifyEmail(@RequestParam(name = "token") String token) {
        authenticationService.verifyEmail(null, null, token);

        return ResponseEntity.status(OK).body(
                new VerifyEmailResponse(getLocalizedMessage("verify_email_success")));
    }

    @Operation(summary = "Sign in", description = "Authenticate user and return token")
    @PostMapping("/sign-in")
    @RateLimit(limitKeyTypes = { BY_IP })
    ResponseEntity<SignInResponse> signIn(@RequestBody @Valid SignInRequest request) {
        User signInUser = authenticationService.signIn(request.email(), request.password());

        String accessToken = authenticationService.generateToken(signInUser, false);

        String refreshToken = authenticationService.generateToken(signInUser, true);

        return ResponseEntity.status(OK).body(
                SignInResponse.builder()
                        .tokens(new Tokens(accessToken, refreshToken))
                        .user(userMapper.toUserInfoResponse(signInUser)).build()
        );
    }

    @Operation(summary = "Refresh", description = "Refresh token")
    @PostMapping("/refresh")
    @RateLimit(limitKeyTypes = { BY_TOKEN })
    ResponseEntity<RefreshResponse> refresh(@RequestBody @Valid RefreshRequest request,
                              HttpServletRequest httpServletRequest) throws ParseException, JOSEException {
        User user = authenticationService.refresh(request.refreshToken(), httpServletRequest);

        String newAccessToken = authenticationService.generateToken(user, false);

        return ResponseEntity.status(OK).body(new RefreshResponse(
                getLocalizedMessage("refresh_token_success"),
                newAccessToken
        ));
    }

    @Operation(summary = "Sign out", description = "Sign out user")
    @PostMapping("/sign-out")
    void signOut(@RequestBody @Valid SignOutRequest request) throws ParseException, JOSEException {
        authenticationService.signOut(request.accessToken(), request.refreshToken());
    }

    @Operation(summary = "Send email forgot password", description = "Send email forgot password")
    @PostMapping("/send-forgot-password")
    ResponseEntity<SendEmailForgotPasswordResponse> sendEmailForgotPassword(
            @RequestBody @Valid SendEmailForgotPasswordRequest request) {
        authenticationService.sendEmailForgotPassword(request.email());

        return ResponseEntity.status(OK).body(new SendEmailForgotPasswordResponse(
                getLocalizedMessage("send_forgot_password_email_success"),
                Date.from(Instant.now().plus(1, ChronoUnit.MINUTES))
        ));
    }

    @Operation(summary = "Verify forgot password code", description = "Verify forgot password code")
    @PostMapping("/forgot-password")
    ResponseEntity<ForgotPasswordResponse> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        User user = userService.findByEmail(request.email());
        String forgotPasswordToken = authenticationService.forgotPassword(user, request.code());

        return ResponseEntity.status(OK).body(new ForgotPasswordResponse(
                getLocalizedMessage("verify_forgot_password_code_success"),
                forgotPasswordToken
        ));
    }

    @Operation(summary = "Reset password", description = "Reset password")
    @PostMapping("/reset-password")
    ResponseEntity<ResetPasswordResponse> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        authenticationService.resetPassword(request.token(), request.password(), request.passwordConfirmation());

        return ResponseEntity.status(OK).body(
                new ResetPasswordResponse(getLocalizedMessage("reset_password_success")));
    }

    @Operation(summary = "Introspect", description = "Introspect provided token")
    @PostMapping("/introspect")
    ResponseEntity<IntrospectResponse> introspect(@RequestBody @Valid IntrospectRequest request)
            throws ParseException, JOSEException {
        boolean isValid = authenticationService.introspect(request.token());

        return ResponseEntity.status(OK).body(new IntrospectResponse(isValid));
    }

}