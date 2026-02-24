package com.company.ppm.auth;

import com.company.ppm.auth.dto.LoginRequest;
import com.company.ppm.auth.dto.TokenResponse;
import com.company.ppm.common.exception.UnauthorizedException;
import com.company.ppm.domain.entity.AppUser;
import com.company.ppm.domain.entity.Organization;
import com.company.ppm.domain.entity.RefreshToken;
import com.company.ppm.repository.UserRepository;
import com.company.ppm.security.JwtTokenProvider;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthService authService;

    @Test
    void loginReturnsTokensWhenCredentialsAreValid() {
        AppUser user = new AppUser();
        user.setId(1L);
        user.setEmail("admin@example.com");
        user.setPasswordHash("hashed");
        user.setActive(true);
        user.setOrganization(new Organization());

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refresh-token");

        Instant expiration = Instant.parse("2026-01-01T00:00:00Z");

        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("AdminPass123!", "hashed")).thenReturn(true);
        when(jwtTokenProvider.generateAccessToken(user)).thenReturn("access-token");
        when(refreshTokenService.issueToken(user)).thenReturn(refreshToken);
        when(jwtTokenProvider.extractExpiration("access-token")).thenReturn(expiration);

        TokenResponse response = authService.login(new LoginRequest("admin@example.com", "AdminPass123!"));

        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-token");
        assertThat(response.accessTokenExpiresAt()).isEqualTo(expiration);
    }

    @Test
    void loginThrowsForInvalidCredentials() {
        AppUser user = new AppUser();
        user.setEmail("admin@example.com");
        user.setPasswordHash("hashed");
        user.setActive(true);

        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginRequest("admin@example.com", "wrong")))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Invalid credentials");
    }
}
