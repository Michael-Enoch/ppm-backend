package com.company.ppm.auth;

import com.company.ppm.auth.dto.LoginRequest;
import com.company.ppm.auth.dto.TokenResponse;
import com.company.ppm.common.exception.UnauthorizedException;
import com.company.ppm.domain.entity.AppUser;
import com.company.ppm.domain.entity.RefreshToken;
import com.company.ppm.repository.UserRepository;
import com.company.ppm.security.JwtTokenProvider;
import java.time.Instant;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider,
            RefreshTokenService refreshTokenService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        AppUser user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!user.isActive() || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.issueToken(user);

        return new TokenResponse(
                accessToken,
                refreshToken.getToken(),
                "Bearer",
                jwtTokenProvider.extractExpiration(accessToken)
        );
    }

    @Transactional
    public TokenResponse refresh(String refreshToken) {
        RefreshToken rotated;
        try {
            rotated = refreshTokenService.rotate(refreshToken);
        } catch (IllegalArgumentException ex) {
            throw new UnauthorizedException(ex.getMessage());
        }
        AppUser user = rotated.getUser();
        String accessToken = jwtTokenProvider.generateAccessToken(user);

        return new TokenResponse(
                accessToken,
                rotated.getToken(),
                "Bearer",
                jwtTokenProvider.extractExpiration(accessToken)
        );
    }

    @Transactional
    public void logout(String refreshToken, String principalEmail) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            refreshTokenService.revoke(refreshToken);
            return;
        }

        if (principalEmail != null) {
            userRepository.findByEmail(principalEmail)
                    .ifPresent(user -> refreshTokenService.revokeAllForUser(user.getId()));
            return;
        }

        throw new UnauthorizedException("No refresh token or authenticated principal provided");
    }
}
