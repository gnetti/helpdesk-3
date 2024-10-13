package com.luiz.helpdesk.infrastructure.adapters.out.config;

import com.luiz.helpdesk.application.ports.in.TokenTimeManagementUseCasePort;
import com.luiz.helpdesk.application.ports.out.JwtTokenProviderPort;
import com.luiz.helpdesk.domain.enums.Profile;
import com.luiz.helpdesk.domain.model.Person;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtTokenProvider implements JwtTokenProviderPort {

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

        Instant validity = now.plusMillis(validityInMilliseconds);

        return Jwts.builder()
                .claim("sub", person.getEmail())
                .claim("id", person.getId())
                .claim("name", person.getName())
                .claim("profile", person.getProfile())
                .claim("theme", person.getTheme())
                .issuedAt(Date.from(now))
                .expiration(Date.from(validity))
                .signWith(key)
                .compact();
    }

    @Override
    public String getEmailFromToken(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getThemeFromToken(String token) {
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        return claims.get("theme", String.class);
    }
}