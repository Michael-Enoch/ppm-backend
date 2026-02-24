package com.company.ppm.dto;

import java.time.Instant;

public record OrganizationResponse(
        Long id,
        String name,
        String code,
        Instant createdAt,
        Instant updatedAt
) {
}
