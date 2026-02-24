package com.company.ppm.auth;

import com.company.ppm.config.AppProperties;
import com.company.ppm.domain.entity.AppUser;
import com.company.ppm.domain.entity.RefreshToken;
import com.company.ppm.repository.RefreshTokenRepository;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final AppProperties appProperties;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, AppProperties appProperties) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.appProperties = appProperties;
    }

    @Transactional
    public RefreshToken issueToken(AppUser user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString() + UUID.randomUUID());
        refreshToken.setExpiresAt(Instant.now().plusSeconds(appProperties.getSecurity().getJwt().getRefreshTokenDays() * 86400));
        refreshToken.setRevoked(false);
        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public RefreshToken rotate(String rawToken) {
        RefreshToken current = validate(rawToken);
        current.setRevoked(true);
        RefreshToken next = issueToken(current.getUser());
        current.setReplacedByToken(next.getToken());
        refreshTokenRepository.save(current);
        return next;
    }

    @Transactional(readOnly = true)
    public RefreshToken validate(String rawToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(rawToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (refreshToken.isRevoked() || refreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Refresh token expired or revoked");
        }
        return refreshToken;
    }

    @Transactional
    public void revoke(String rawToken) {
        refreshTokenRepository.findByToken(rawToken).ifPresent(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        });
    }

    @Transactional
    public void revokeAllForUser(Long userId) {
        refreshTokenRepository.revokeAllByUserId(userId);
    }
}
