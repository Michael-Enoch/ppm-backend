package com.company.ppm.dto;

import java.math.BigDecimal;

public record KpiUpdateRequest(
        String name,
        String description,
        String unit,
        BigDecimal targetValue
) {
}
