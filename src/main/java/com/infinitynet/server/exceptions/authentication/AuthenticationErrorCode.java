package com.infinitynet.server.exceptions.authentication;

import lombok.Getter;

@Getter
public enum AuthenticationErrorCode {
    // Validation Errors
    VALIDATION_ERROR("auth/validation-error", "validation_error"),
    INVALID_EMAIL("auth/invalid-email", "invalid_email"),
    WEAK_PASSWORD("auth/weak-password", "weak_password"),
    PASSWORD_MIS_MATCH("auth/password-mismatch", "password_mis_match"),
    TERMS_NOT_ACCEPTED("auth/terms-not-accepted", "terms_not_accepted"),

    // Authentication Errors
    WRONG_PASSWORD("auth/wrong-password", "wrong_password"),
    EXPIRED_PASSWORD("auth/expired-password", "expired_password"),
    TWO_FACTOR_REQUIRED("auth/two-factor-required", "two_factor_required"),
    INVALID_ACTIVATION_CODE("auth/invalid-activation-code", "invalid_activation_code"),

    // Token Errors
    TOKEN_MISSING("auth/token-missing", "token_missing"),
    TOKEN_INVALID("auth/token-invalid", "token_invalid"),
    TOKEN_EXPIRED("auth/token-expired", "token_expired"),
    INVALID_TOKEN("auth/invalid-token", "invalid_token"),
    TOKEN_REVOKED("auth/token-revoked", "token_revoked"),
    TOKEN_BLACKLISTED("auth/token-blacklisted", "token_blacklisted"),
    INVALID_SIGNATURE("auth/invalid-signature", "invalid_signature"),

    // Verification Errors
    CODE_INVALID("auth/code-invalid", "code_invalid"),

    // User Errors
    USER_DISABLED("auth/user-disabled", "user_disabled"),
    USER_NOT_ACTIVATED("auth/user-not-activated", "user_not_activated"),
    USER_NOT_FOUND("auth/user-not-found", "user_not_found"),
    EMAIL_ALREADY_IN_USE("auth/email-already-in-use", "email_already_in_use"),
    USER_ALREADY_VERIFIED("auth/user-already-verified", "user_already_verified"),
    CANNOT_SEND_EMAIL("auth/cannot-send-email", "cannot_send_email"),

    //Rate Limiting Errors
    TOO_MANY_REQUESTS("auth/too-many-requests", "too_many_requests"),
    RATE_LIMIT_EXCEEDED("auth/rate-limit-exceeded", "rate_limit_exceeded"),
    ;

    AuthenticationErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    private final String code;
    private final String message;

}