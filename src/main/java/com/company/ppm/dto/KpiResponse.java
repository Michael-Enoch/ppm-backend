package com.company.ppm.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record KpiResponse(
        Long id,
        String name,
        String description,
        String unit,
        BigDecimal targetValue,
        Long organizationId,
        Instant createdAt,
        Instant updatedAt
) {
}
