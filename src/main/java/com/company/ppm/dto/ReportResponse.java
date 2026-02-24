package com.company.ppm.dto;

import com.company.ppm.domain.enums.ReportFormat;
import com.company.ppm.domain.enums.ReportStatus;
import java.time.Instant;

public record ReportResponse(
        Long id,
        ReportFormat format,
        ReportStatus status,
        String downloadUrl,
        String errorMessage,
        Instant createdAt,
        Instant updatedAt
) {
}
