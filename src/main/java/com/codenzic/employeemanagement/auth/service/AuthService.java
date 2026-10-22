package com.codenzic.employeemanagement.auth.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

import com.codenzic.employeemanagement.auth.dto.LoginRequest;
import com.codenzic.employeemanagement.auth.dto.LoginResponse;
import com.codenzic.employeemanagement.config.AuthProperties;

@Service
public class AuthService {

    private final AuthProperties authProperties;
    private final JwtEncoder jwtEncoder;
    private final long tokenExpirationMillis;

    public AuthService(AuthProperties authProperties,JwtEncoder jwtEncoder,
            @Value("${app.jwt.expiration}") long tokenExpirationMillis) {

        this.authProperties = authProperties;
        this.jwtEncoder = jwtEncoder;
        this.tokenExpirationMillis = tokenExpirationMillis;
    }

    public LoginResponse login(LoginRequest request) {
        if (!authProperties.username().equals(request.username())
                || !passwordMatches(request.password(), authProperties.password())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusMillis(tokenExpirationMillis);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("employee-management-api")
                .subject(authProperties.username())
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .claim("roles", List.of("ADMIN"))
                .build();

        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
        long expiresInSeconds = tokenExpirationMillis / 1_000;

        return new LoginResponse(token, "Bearer", expiresInSeconds);
    }

    private boolean passwordMatches(String submittedPassword, String configuredPassword) {
        return MessageDigest.isEqual(
                submittedPassword.getBytes(StandardCharsets.UTF_8),
                configuredPassword.getBytes(StandardCharsets.UTF_8)
        );
    }
}
