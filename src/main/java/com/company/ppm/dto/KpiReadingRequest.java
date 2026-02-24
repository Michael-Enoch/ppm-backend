package com.company.ppm.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;

public record KpiReadingRequest(
        @NotNull BigDecimal value,
        Instant observedAt,
        String source
) {
}
