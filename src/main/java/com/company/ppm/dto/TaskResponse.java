package com.company.ppm.dto;

import java.time.Instant;
import java.time.LocalDate;

public record TaskResponse(
        Long id,
        Long projectId,
        String title,
        String description,
        String status,
        String priority,
        Long assigneeId,
        Integer orderIndex,
        LocalDate dueDate,
        Instant createdAt,
        Instant updatedAt
) {
}
