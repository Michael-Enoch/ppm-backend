package com.company.ppm.dto;

import com.company.ppm.domain.enums.TaskPriority;
import com.company.ppm.domain.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record TaskCreateRequest(
        @NotBlank String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        Long assigneeId,
        LocalDate dueDate
) {
}
