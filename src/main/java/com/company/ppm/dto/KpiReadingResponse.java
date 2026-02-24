package com.company.ppm.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record KpiReadingResponse(
        Long id,
        Long kpiId,
        BigDecimal value,
        Instant observedAt,
        String source,
        Instant createdAt
) {
}
