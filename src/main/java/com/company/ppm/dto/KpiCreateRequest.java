package com.company.ppm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record KpiCreateRequest(
        @NotBlank String name,
        String description,
        @NotBlank String unit,
        BigDecimal targetValue,
        @NotNull Long organizationId
) {
}
