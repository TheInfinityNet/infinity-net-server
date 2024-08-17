package com.infinitynet.server.services;

import java.text.ParseException;

import com.infinitynet.server.dtos.requests.authentication.*;
import com.infinitynet.server.dtos.responses.authentication.*;
import com.infinitynet.server.entities.User;
import com.infinitynet.server.enums.VerificationType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.*;

@Service
public interface AuthenticationService {

    boolean introspect(String token) throws JOSEException, ParseException;

    void signUp(User user, String confirmationPassword);

    void sendEmailVerification(String email, VerificationType verificationType);

    void verifyEmail(User user, String code, String token);

    User signIn(String email, String password);

    String generateToken(User user, boolean isRefresh);

    User refresh(String refreshToken, HttpServletRequest servletRequest) throws ParseException, JOSEException;

    void sendEmailForgotPassword(String email);

    String forgotPassword(User user, String code);

    void resetPassword(String token, String password, String confirmationPassword);

    void signOut(String accessToken, String refreshToken) throws ParseException, JOSEException;

}