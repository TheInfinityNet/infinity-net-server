package com.infinitynet.server.services.impls;

import com.infinitynet.server.entities.User;
import com.infinitynet.server.entities.Verification;
import com.infinitynet.server.enums.VerificationType;
import com.infinitynet.server.exceptions.authentication.AuthenticationException;
import com.infinitynet.server.repositories.VerificationRepository;
import com.infinitynet.server.services.AuthenticationService;
import com.infinitynet.server.services.BaseRedisService;
import com.infinitynet.server.services.UserService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.infinitynet.server.components.Translator.getLocalizedMessage;
import static com.infinitynet.server.Constants.*;
import static com.infinitynet.server.exceptions.authentication.AuthenticationErrorCode.*;
import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceImpl implements AuthenticationService {

    UserService userService;

    VerificationRepository verificationRepository;

    PasswordEncoder passwordEncoder;

    KafkaTemplate<String, String> kafkaTemplate;

    BaseRedisService<String, String, Object> baseRedisService;

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

    @Override
    public boolean introspect(String token) throws JOSEException, ParseException {
        boolean isValid = true;

        try {
            verifyToken(token, false);

        } catch (AuthenticationException e) {
            isValid = false;
        }

        return isValid;
    }

    @Override
    public void signUp(User user, String confirmationPassword) {
        if (userService.existsByEmail(user.getEmail()))
            throw new AuthenticationException(EMAIL_ALREADY_IN_USE, CONFLICT);

        if (!user.getPassword().equals(confirmationPassword))
            throw new AuthenticationException(PASSWORD_MIS_MATCH, BAD_REQUEST);

        if (isInvalidEmail(user.getEmail()))
            throw new AuthenticationException(INVALID_EMAIL, BAD_REQUEST);

        if (isWeakPassword(user.getPassword()))
            throw new AuthenticationException(WEAK_PASSWORD, BAD_REQUEST);

        if (isTermsNotAccepted())
            throw new AuthenticationException(TERMS_NOT_ACCEPTED, BAD_REQUEST);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        try {
            userService.createUser(user);

        } catch (DataIntegrityViolationException exception) {
            throw new AuthenticationException(EMAIL_ALREADY_IN_USE, CONFLICT);
        }
    }

    @Override
    @Transactional
    public void sendEmailVerification(String email, VerificationType verificationType) {
        User user = userService.findByEmail(email);

        List<Verification> verifications = verificationRepository.findByUserAndVerificationType(user, verificationType);

        if (verificationType.equals(VerificationType.VERIFY_EMAIL_BY_CODE)
                || verificationType.equals(VerificationType.VERIFY_EMAIL_BY_TOKEN)) {
            if (user.isActivated())
                throw new AuthenticationException(USER_ALREADY_VERIFIED, BAD_REQUEST);

            else {
                if (!verifications.isEmpty()) verificationRepository.deleteAll(verifications);

                sendEmail(email, verificationType);
            }

        } else {
            if (verifications.isEmpty()) throw new AuthenticationException(CANNOT_SEND_EMAIL, BAD_REQUEST);

            else {
                verificationRepository.deleteAll(verifications);
                sendEmail(email, verificationType);
            }
        }
    }

    @Override
    @Transactional
    public void verifyEmail(User user, String code, String token) {
        Verification verification = (code != null)
                ? verificationRepository.findByCode(code).orElseThrow(() ->
                new AuthenticationException(CODE_INVALID, BAD_REQUEST))

                : verificationRepository.findById(token).orElseThrow(() ->
                new AuthenticationException(CODE_INVALID, BAD_REQUEST));

        if (verification.getExpiryTime().before(new Date()))
            throw new AuthenticationException(CODE_INVALID, UNPROCESSABLE_ENTITY);

        userService.activateUser((user != null) ? user : verification.getUser());

        verificationRepository.delete(verification);
    }

    @Override
    public User signIn(String email, String password) {
        User user = userService.findByEmail(email);

        if (isPasswordExpired(user))
            throw new AuthenticationException(EXPIRED_PASSWORD, CONFLICT);

        if (isTwoFactorRequired(user))
            throw new AuthenticationException(TWO_FACTOR_REQUIRED, FORBIDDEN);

        if (isUserDisabled(user))
            throw new AuthenticationException(USER_DISABLED, FORBIDDEN);

        if (!passwordEncoder.matches(password, user.getPassword()))
            throw new AuthenticationException(WRONG_PASSWORD, UNAUTHORIZED);

        if (!user.isActivated()) throw new AuthenticationException(USER_NOT_ACTIVATED, FORBIDDEN);

        return user;
    }

    @Override
    public String generateToken(User user, boolean isRefresh) {
        JWSHeader accessHeader = new JWSHeader(ACCESS_TOKEN_SIGNATURE_ALGORITHM);
        JWSHeader refreshHeader = new JWSHeader(REFRESH_TOKEN_SIGNATURE_ALGORITHM);

        Date expiryTime = (isRefresh)
                ? new Date(Instant.now().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli())
                : new Date(Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli());

        String jwtID = UUID.randomUUID().toString();

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

    @Override
    public User refresh(String refreshToken, HttpServletRequest servletRequest) throws ParseException, JOSEException {
        SignedJWT signedJWT = verifyToken(refreshToken, true);
        String email = signedJWT.getJWTClaimsSet().getSubject();

        User user;
        try {
            user = userService.findByEmail(email);

        } catch (AuthenticationException e) {
            throw new AuthenticationException(INVALID_TOKEN, BAD_REQUEST);
        }

        if (servletRequest.getHeader("Authorization") == null)
            throw new AuthenticationException(INVALID_TOKEN, BAD_REQUEST);

        String accessToken = servletRequest.getHeader("Authorization").substring(7);
        SignedJWT signedAccessTokenJWT = SignedJWT.parse(accessToken);
        String jwtID = signedAccessTokenJWT.getJWTClaimsSet().getJWTID();
        Date expiryTime = signedAccessTokenJWT.getJWTClaimsSet().getExpirationTime();

        if (!signedAccessTokenJWT.getJWTClaimsSet().getSubject().equals(email))
            throw new AuthenticationException(INVALID_TOKEN, BAD_REQUEST);

        if (expiryTime.after(new Date())) {
            baseRedisService.set(jwtID, "revoked");
            baseRedisService.setTimeToLive(jwtID, expiryTime.getTime() - System.currentTimeMillis());
        }

        return user;
    }

    @Override
    @Transactional
    public void sendEmailForgotPassword(String email) {
        sendEmail(email, VerificationType.RESET_PASSWORD);
    }

    @Override
    @Transactional
    public String forgotPassword(User user, String code) {
        Verification verification = verificationRepository.findByCode(code).orElseThrow(() ->
                new AuthenticationException(CODE_INVALID, BAD_REQUEST));

        if (verification.getExpiryTime().before(new Date()))
            throw new AuthenticationException(CODE_INVALID, UNPROCESSABLE_ENTITY);

        if (!verification.getUser().getEmail().equals(user.getEmail()))
            throw new AuthenticationException(CODE_INVALID, BAD_REQUEST);

        return verification.getToken();
    }

    @Override
    @Transactional
    public void resetPassword(String token, String password, String confirmationPassword) {
        Verification verification = verificationRepository.findById(token).orElseThrow(() ->
                new AuthenticationException(TOKEN_REVOKED, UNPROCESSABLE_ENTITY));

        if (verification.getExpiryTime().before(new Date()))
            throw new AuthenticationException(TOKEN_EXPIRED, UNPROCESSABLE_ENTITY);

        if (!password.equals(confirmationPassword))
            throw new AuthenticationException(PASSWORD_MIS_MATCH, BAD_REQUEST);

        if (isWeakPassword(password))
            throw new AuthenticationException(WEAK_PASSWORD, BAD_REQUEST);

        User user = verification.getUser();
        userService.updatePassword(user, passwordEncoder.encode(password));
        verificationRepository.delete(verification);
    }

    @Override
    public void signOut(String accessToken, String refreshToken) throws ParseException, JOSEException {
        try {
            SignedJWT signAccessToken = verifyToken(accessToken, false);
            Date AccessTokenExpiryTime = signAccessToken.getJWTClaimsSet().getExpirationTime();

            if (AccessTokenExpiryTime.after(new Date())) {
                baseRedisService.set(signAccessToken.getJWTClaimsSet().getJWTID(), "revoked");
                baseRedisService.setTimeToLive(signAccessToken.getJWTClaimsSet().getJWTID(),
                        AccessTokenExpiryTime.getTime() - System.currentTimeMillis());
            }

            SignedJWT signRefreshToken = verifyToken(refreshToken, true);
            Date RefreshTokenExpiryTime = signRefreshToken.getJWTClaimsSet().getExpirationTime();

            if (RefreshTokenExpiryTime.after(new Date())) {
                baseRedisService.set(signRefreshToken.getJWTClaimsSet().getJWTID(), "revoked");
                baseRedisService.setTimeToLive(signRefreshToken.getJWTClaimsSet().getJWTID(),
                        RefreshTokenExpiryTime.getTime() - System.currentTimeMillis());
            }

        } catch (AuthenticationException exception) {
            log.error("Cannot sign out", exception);
            //TODO: Disable the user account
        }
    }

    @Transactional
    protected void sendEmail(String email, VerificationType verificationType) {
        User user = userService.findByEmail(email);

        Verification verification = verificationRepository.save(Verification.builder()
                .code(generateVerificationCode(6))
                .expiryTime(Date.from(Instant.now().plus(3, ChronoUnit.MINUTES)))
                .verificationType(verificationType)
                .user(user)
                .build());

        kafkaTemplate.send(KAFKA_TOPIC_SEND_MAIL,
                verificationType + ":" + email + ":" + verification.getToken() + ":" + verification.getCode());
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = (isRefresh)
                ? new MACVerifier(REFRESH_SIGNER_KEY.getBytes())
                : new MACVerifier(ACCESS_SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        boolean verified = signedJWT.verify(verifier);

        if (isRefresh) {
            if (expiryTime.before(new Date()))
                throw new AuthenticationException(TOKEN_EXPIRED, UNAUTHORIZED);

            if (!verified)
                throw new AuthenticationException(INVALID_SIGNATURE, UNAUTHORIZED);

            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    REFRESH_SIGNER_KEY.getBytes(),
                    REFRESH_TOKEN_SIGNATURE_ALGORITHM.getName()
            );
            try {
                NimbusJwtDecoder nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                        .macAlgorithm(MacAlgorithm.from(REFRESH_TOKEN_SIGNATURE_ALGORITHM.getName()))
                        .build();
                nimbusJwtDecoder.decode(token);

            } catch (JwtException e) {
                throw new AuthenticationException(INVALID_SIGNATURE, UNAUTHORIZED);
            }

        } else {
            if (!verified || expiryTime.before(new Date()))
                throw new AuthenticationException(TOKEN_INVALID, UNAUTHORIZED);
        }

        String value = (String) baseRedisService.get(signedJWT.getJWTClaimsSet().getJWTID());

        if (value != null) {
            if (value.equals("revoked"))
                throw new AuthenticationException(TOKEN_REVOKED, UNAUTHORIZED);

            else
                throw new AuthenticationException(TOKEN_BLACKLISTED, UNAUTHORIZED);
        }

        return signedJWT;
    }

    public static String generateVerificationCode(int length) {
        StringBuilder code = new StringBuilder();
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(randomIndex));
        }

        return code.toString();
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

}