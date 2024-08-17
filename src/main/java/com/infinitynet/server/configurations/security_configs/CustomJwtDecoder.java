package com.infinitynet.server.configurations.security_configs;

import java.text.ParseException;
import java.util.Objects;
import javax.crypto.spec.SecretKeySpec;

import com.infinitynet.server.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.JOSEException;

import static com.infinitynet.server.Constants.ACCESS_TOKEN_SIGNATURE_ALGORITHM;

@Component
public class CustomJwtDecoder implements JwtDecoder {

    @Value("${jwt.accessSignerKey}")
    private String ACCESS_SIGNER_KEY;

    @Autowired
    private AuthenticationService authenticationService;

    private NimbusJwtDecoder nimbusJwtDecoder = null;

    @Override
    public Jwt decode(String token) throws JwtException {

        try {
            if (!authenticationService.introspect(token)) throw new JwtException("Token invalid");

        } catch (JOSEException | ParseException e) {
            throw new JwtException(e.getMessage());
        }

        if (Objects.isNull(nimbusJwtDecoder)) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(ACCESS_SIGNER_KEY.getBytes(), ACCESS_TOKEN_SIGNATURE_ALGORITHM.getName());
            nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.from(ACCESS_TOKEN_SIGNATURE_ALGORITHM.getName()))
                    .build();
        }

        return nimbusJwtDecoder.decode(token);
    }

}