package com.company.ppm.dto;

import com.company.ppm.domain.enums.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProjectCreateRequest(
        @NotBlank String name,
        String description,
        @NotNull Long organizationId,
        ProjectStatus status
) {
}
