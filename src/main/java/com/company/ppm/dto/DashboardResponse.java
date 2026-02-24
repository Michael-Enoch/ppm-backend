package com.company.ppm.dto;

import java.time.Instant;

public record DashboardResponse(
        long totalProjects,
        long activeProjects,
        long totalTasks,
        long completedTasks,
        long totalKpis,
        Instant generatedAt
) {
}
