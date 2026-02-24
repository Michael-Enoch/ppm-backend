package com.company.ppm.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record TaskOrderRequest(@NotEmpty List<Long> taskIds) {
}
