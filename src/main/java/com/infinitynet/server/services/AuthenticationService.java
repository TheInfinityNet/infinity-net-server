package com.infinitynet.server.services;

import java.text.ParseException;

import com.infinitynet.server.dtos.requests.authentication.*;
import com.infinitynet.server.dtos.responses.authentication.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.*;

@Service
public interface AuthenticationService {

    IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException;

    SignInResponse signIn(SignInRequest request);

    SignUpResponse signUp(SignUpRequest request);

    void signOut(SignOutRequest request) throws ParseException, JOSEException;

    SendEmailVerificationResponse sendEmailVerification(SendEmailVerificationRequest request);

    VerifyEmailResponse verifyEmail(VerifyEmailByCodeRequest request, String token);

    RefreshResponse refresh(RefreshRequest request,
                            HttpServletRequest servletRequest) throws ParseException, JOSEException;

    SendEmailForgotPasswordResponse sendEmailForgotPassword(SendEmailForgotPasswordRequest request);

    ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request);

    ResetPasswordResponse resetPassword(ResetPasswordRequest request);

}