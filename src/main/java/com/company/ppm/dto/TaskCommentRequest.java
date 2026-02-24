package com.company.ppm.dto;

import jakarta.validation.constraints.NotBlank;

public record TaskCommentRequest(@NotBlank String comment) {
}
