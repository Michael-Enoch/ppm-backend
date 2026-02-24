package com.company.ppm.security;

import com.company.ppm.config.AppProperties;
import com.company.ppm.domain.entity.AppUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private final AppProperties appProperties;

    public JwtTokenProvider(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    public String generateAccessToken(AppUser user) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(appProperties.getSecurity().getJwt().getAccessTokenMinutes() * 60);

        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList());

        return Jwts.builder()
                .subject(user.getEmail())
                .issuer(appProperties.getSecurity().getJwt().getIssuer())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .claim("uid", user.getId())
                .claim("roles", roles)
                .signWith(signingKey())
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public Instant extractExpiration(String token) {
        return parseClaims(token).getExpiration().toInstant();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey signingKey() {
        String secret = appProperties.getSecurity().getJwt().getSecret();
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
