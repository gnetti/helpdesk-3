package com.luiz.helpdesk.infrastructure.adapters.out.config;

import com.luiz.helpdesk.application.ports.in.TokenTimeManagementUseCasePort;
import com.luiz.helpdesk.application.ports.out.JwtTokenProviderPort;
import com.luiz.helpdesk.domain.enums.Profile;
import com.luiz.helpdesk.domain.model.Person;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtTokenProvider implements JwtTokenProviderPort {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final SecretKey key;
    private final TokenTimeManagementUseCasePort tokenTimeManageService;

    public JwtTokenProvider(
            @Value("${security.jwt.token.secret-key}") String secretKey,
            @Lazy TokenTimeManagementUseCasePort tokenTimeManageService
    ) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.tokenTimeManageService = tokenTimeManageService;
    }

    @Override
    public String createToken(Person person) {
        Instant now = Instant.now();
        Profile profile = Profile.fromCode(person.getProfile());
        long validityInMilliseconds = tokenTimeManageService.getExpirationTimeInMillis(profile);

        logger.info("Creating token for user: {}, id: {}, profile: {}", person.getEmail(), person.getId(), profile);
        logger.info("Token validity in milliseconds from TokenTimeManageService: {}", validityInMilliseconds);

        Instant validity = now.plusMillis(validityInMilliseconds);

        logger.info("Token expiration date: {}", validity);
        logger.info("Token validity duration: {} minutes", ChronoUnit.MINUTES.between(now, validity));

        String token = Jwts.builder()
                .claim("sub", person.getEmail())
                .claim("id", person.getId())
                .claim("name", person.getName())
                .claim("profile", person.getProfile())
                .claim("theme", person.getTheme())
                .issuedAt(Date.from(now))
                .expiration(Date.from(validity))
                .signWith(key)
                .compact();

        logger.info("Token created successfully");
        logger.info("Token issued at: {}", now);
        logger.info("Token expires at: {}", validity);
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        logger.info("Token claims: {}", claims);

        return token;
    }

    @Override
    public String getEmailFromToken(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
            logger.info("Validating token. Expiration: {}", claims.getExpiration());
            return true;
        } catch (Exception e) {
            logger.error("Failed to validate token", e);
            return false;
        }
    }

    @Override
    public String getThemeFromToken(String token) {
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        return claims.get("theme", String.class);
    }
}