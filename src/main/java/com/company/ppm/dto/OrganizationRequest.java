package com.company.ppm.dto;

import jakarta.validation.constraints.NotBlank;

public record OrganizationRequest(
        @NotBlank String name,
        @NotBlank String code
) {
}
