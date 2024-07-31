package com.infinitynet.server.services;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import com.infinitynet.server.dtos.others.Tokens;
import com.infinitynet.server.dtos.requests.*;
import com.infinitynet.server.dtos.responses.*;
import com.infinitynet.server.entities.InvalidatedToken;
import com.infinitynet.server.entities.User;
import com.infinitynet.server.exceptions.AuthenticationExceptions;
import com.infinitynet.server.mappers.UserMapper;
import com.infinitynet.server.repositories.InvalidatedTokenRepository;
import com.infinitynet.server.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

import static com.infinitynet.server.constants.Constants.KAFKA_TOPIC_SEND_MAIL;
import static com.infinitynet.server.exceptions.AuthenticationErrorCodes.*;
import static com.infinitynet.server.exceptions.AuthenticationErrorCodes.TOO_MANY_REQUESTS;
import static com.nimbusds.jose.JWSAlgorithm.*;
import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {

    UserRepository userRepository;

    InvalidatedTokenRepository invalidatedTokenRepository;

    PasswordEncoder passwordEncoder;

    UserMapper userMapper = UserMapper.INSTANCE;

    KafkaTemplate<String, String> kafkaTemplate;

    @NonFinal
    @Value("${jwt.accessSignerKey}")
    protected String ACCESS_SIGNER_KEY;

    @NonFinal
    @Value("${jwt.refreshSignerKey}")
    protected String REFRESH_SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        String token = request.token();
        boolean isValid = true;

        try {
            verifyToken(token, false);

        } catch (AuthenticationExceptions e) {
            isValid = false;
        }

        return IntrospectResponse.builder().valid(isValid).build();
    }

    public SignInResponse signIn(SignInRequest request) {
        if (isTooManyRequests())
            throw new AuthenticationExceptions(TOO_MANY_REQUESTS, HttpStatus.TOO_MANY_REQUESTS);

        if (request.email().equals("invalidation@infinity.net") && request.password().equals("password"))
            throw new AuthenticationExceptions(VALIDATION_ERROR, UNPROCESSABLE_ENTITY);

        User user = userRepository
                .findByEmail(request.email())
                .orElseThrow(() -> new AuthenticationExceptions(USER_NOT_FOUND, NOT_FOUND));

        if (isPasswordExpired(user))
            throw new AuthenticationExceptions(EXPIRED_PASSWORD, CONFLICT);

        if (isTwoFactorRequired(user))
            throw new AuthenticationExceptions(TWO_FACTOR_REQUIRED, FORBIDDEN);

        if (isUserDisabled(user))
            throw new AuthenticationExceptions(USER_DISABLED, FORBIDDEN);

        if (!passwordEncoder.matches(request.password(), user.getPassword()))
            throw new AuthenticationExceptions(WRONG_PASSWORD, UNAUTHORIZED);

        if (!user.isActivated()) throw new AuthenticationExceptions(USER_NOT_ACTIVATED, FORBIDDEN);

        String accessToken = generateToken(user, false);
        String refreshToken = generateToken(user, true);

        return SignInResponse.builder()
                .tokens(new Tokens(accessToken, refreshToken))
                .user(userMapper.toUserResponse(user)).build();
    }

    @Transactional
    public UserResponse signUp(UserCreationRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent())
            throw new AuthenticationExceptions(EMAIL_ALREADY_IN_USE, CONFLICT);

        if (!request.password().equals(request.passwordConfirmation()))
            throw new AuthenticationExceptions(PASSWORD_MIS_MATCH, BAD_REQUEST);

        if (isInvalidEmail(request.email()))
            throw new AuthenticationExceptions(INVALID_EMAIL, BAD_REQUEST);

        if (isWeakPassword(request.password()))
            throw new AuthenticationExceptions(WEAK_PASSWORD, BAD_REQUEST);

        if (isTermsNotAccepted())
            throw new AuthenticationExceptions(TERMS_NOT_ACCEPTED, BAD_REQUEST);

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setActivated(false);

        String verificationCode = UUID.randomUUID().toString();
        user.setVerificationCode(passwordEncoder.encode(verificationCode));

        try {
            user = userRepository.save(user);

            kafkaTemplate.send(KAFKA_TOPIC_SEND_MAIL, "new-user:" + user.getId() + ":" + verificationCode);

        } catch (DataIntegrityViolationException exception) {
            throw new AuthenticationExceptions(EMAIL_ALREADY_IN_USE, CONFLICT);
        }

        return userMapper.toUserResponse(user);
    }

    public void signOut(SignOutRequest request) throws ParseException, JOSEException {
        try {
            SignedJWT signAccessToken = verifyToken(request.accessToken(), false);
            Date AccessTokenExpiryTime = signAccessToken.getJWTClaimsSet().getExpirationTime();

            if (AccessTokenExpiryTime.after(new Date())) {
                InvalidatedToken invalidatedAccessToken = InvalidatedToken.builder()
                        .id(signAccessToken.getJWTClaimsSet().getJWTID())
                        .expiryTime(AccessTokenExpiryTime).build();

                invalidatedTokenRepository.save(invalidatedAccessToken);
            }

            SignedJWT signRefreshToken = verifyToken(request.refreshToken(), true);
            Date RefreshTokenExpiryTime = signRefreshToken.getJWTClaimsSet().getExpirationTime();

            if (RefreshTokenExpiryTime.after(new Date())) {
                InvalidatedToken invalidatedRefreshToken = InvalidatedToken.builder()
                        .id(signRefreshToken.getJWTClaimsSet().getJWTID())
                        .expiryTime(RefreshTokenExpiryTime).build();

                invalidatedTokenRepository.save(invalidatedRefreshToken);
            }

        } catch (AuthenticationExceptions exception) {
            log.error("Cannot sign out", exception);
            //TODO: Disable the user account
        }
    }

    public RefreshResponse refreshToken(RefreshRequest refreshRequest, HttpServletRequest servletRequest)
            throws ParseException, JOSEException {
        SignedJWT signedJWT = verifyToken(refreshRequest.refreshToken(), true);
        String email = signedJWT.getJWTClaimsSet().getSubject();

        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new AuthenticationExceptions(INVALID_TOKEN, BAD_REQUEST));

        if (servletRequest.getHeader("Authorization") == null)
            throw new AuthenticationExceptions(INVALID_TOKEN, BAD_REQUEST);

        String accessToken = servletRequest.getHeader("Authorization").substring(7);
        SignedJWT signedAccessTokenJWT = SignedJWT.parse(accessToken);
        String jwtID = signedAccessTokenJWT.getJWTClaimsSet().getJWTID();
        Date expiryTime = signedAccessTokenJWT.getJWTClaimsSet().getExpirationTime();

        if (!signedAccessTokenJWT.getJWTClaimsSet().getSubject().equals(email))
            throw new AuthenticationExceptions(INVALID_TOKEN, BAD_REQUEST);

        if (expiryTime.after(new Date())) {
            InvalidatedToken invalidatedToken =
                    InvalidatedToken.builder().id(jwtID).expiryTime(expiryTime).build();

            invalidatedTokenRepository.save(invalidatedToken);
        }

        return new RefreshResponse(generateToken(user, false));
    }

    private String generateToken(User user, boolean isRefresh) {
        JWSHeader accessHeader = new JWSHeader(HS512);
        JWSHeader refreshHeader = new JWSHeader(HS384);

        Date expiryTime = (isRefresh)
                ? new Date(Instant.now().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli())
                : new Date(Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli());

        String jwtID = (isRefresh)
                ? "RC_" + UUID.randomUUID()
                : "AC_" + UUID.randomUUID();

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("com.infinitynet")
                .issueTime(new Date())
                .expirationTime(expiryTime)
                .jwtID(jwtID)
                .build();

        if (!isRefresh) {
            jwtClaimsSet = new JWTClaimsSet.Builder(jwtClaimsSet)
                    .claim("more-info", "???????")
                    .build();
        }

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = (isRefresh)
                ? new JWSObject(refreshHeader, payload)
                : new JWSObject(accessHeader, payload);

        try {
            if (isRefresh)
                jwsObject.sign(new MACSigner(REFRESH_SIGNER_KEY.getBytes()));
            else
                jwsObject.sign(new MACSigner(ACCESS_SIGNER_KEY.getBytes()));

            return jwsObject.serialize();

        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        if (isRateLimitExceeded())
            throw new AuthenticationExceptions(RATE_LIMIT_EXCEEDED, HttpStatus.TOO_MANY_REQUESTS);

        JWSVerifier verifier = (isRefresh)
                ? new MACVerifier(REFRESH_SIGNER_KEY.getBytes())
                : new MACVerifier(ACCESS_SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        boolean verified = signedJWT.verify(verifier);

        if (isRefresh) {
            if (expiryTime.before(new Date()))
                throw new AuthenticationExceptions(TOKEN_EXPIRED, UNAUTHORIZED);

            if (!verified)
                throw new AuthenticationExceptions(INVALID_SIGNATURE, UNAUTHORIZED);

        } else {
            if (!verified || expiryTime.before(new Date()))
                throw new AuthenticationExceptions(TOKEN_INVALID, UNAUTHORIZED);
        }

        Optional<InvalidatedToken> invalidatedToken =
                invalidatedTokenRepository.findById(signedJWT.getJWTClaimsSet().getJWTID());

        if (invalidatedToken.isPresent()) {
            if (invalidatedToken.get().isBlacklisted())
                throw new AuthenticationExceptions(TOKEN_BLACKLISTED, UNAUTHORIZED);

            else
                throw new AuthenticationExceptions(TOKEN_REVOKED, UNAUTHORIZED);
        }

        return signedJWT;
    }

    @Transactional
    public ActivationResponse activate(String id, String activationCode) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new AuthenticationExceptions(USER_NOT_FOUND, NOT_FOUND));

        if (!passwordEncoder.matches(activationCode, user.getVerificationCode())) {
            throw new AuthenticationExceptions(INVALID_ACTIVATION_CODE, BAD_REQUEST);
        }

        user.setActivated(true);
        user.setVerificationCode(null);

        userRepository.save(user);

        return new ActivationResponse(true);
    }

    private boolean isTooManyRequests() {
        return false;
    }

    private boolean isPasswordExpired(User user) {
        return false;
    }

    private boolean isTwoFactorRequired(User user) {
        return false;
    }

    private boolean isUserDisabled(User user) {
        return false;
    }

    private boolean isTermsNotAccepted() {
        return false;
    }

    private boolean isInvalidEmail(String email) {
        return false;
    }

    private boolean isWeakPassword(String password) {
        return false;
    }

    private boolean isRateLimitExceeded() {
        return false;
    }

}