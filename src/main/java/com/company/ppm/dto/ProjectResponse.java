package com.company.ppm.dto;

import com.company.ppm.domain.enums.ProjectStatus;
import java.time.Instant;

public record ProjectResponse(
        Long id,
        String name,
        String description,
        ProjectStatus status,
        Long organizationId,
        Long createdBy,
        Instant createdAt,
        Instant updatedAt
) {
}
