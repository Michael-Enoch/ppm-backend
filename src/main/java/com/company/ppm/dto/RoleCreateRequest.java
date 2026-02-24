package com.company.ppm.dto;

import com.company.ppm.domain.enums.RoleName;
import jakarta.validation.constraints.NotNull;

public record RoleCreateRequest(@NotNull RoleName name) {
}
