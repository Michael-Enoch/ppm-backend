package com.company.ppm.dto;

import com.company.ppm.domain.enums.ReportFormat;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

public record ReportRequest(
        @NotNull ReportFormat format,
        Map<String, Object> parameters
) {
}
