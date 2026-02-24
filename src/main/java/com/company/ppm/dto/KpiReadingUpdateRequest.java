package com.company.ppm.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record KpiReadingUpdateRequest(
        BigDecimal value,
        Instant observedAt,
        String source
) {
}
