package com.company.ppm.dto;

import com.company.ppm.domain.enums.ProjectStatus;

public record ProjectUpdateRequest(
        String name,
        String description,
        ProjectStatus status
) {
}
