package com.company.ppm.auth.dto;

import java.time.Instant;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Instant accessTokenExpiresAt
) {
}
