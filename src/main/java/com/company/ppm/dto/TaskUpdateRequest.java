package com.company.ppm.dto;

import com.company.ppm.domain.enums.TaskPriority;
import com.company.ppm.domain.enums.TaskStatus;
import java.time.LocalDate;

public record TaskUpdateRequest(
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        Long assigneeId,
        LocalDate dueDate
) {
}
