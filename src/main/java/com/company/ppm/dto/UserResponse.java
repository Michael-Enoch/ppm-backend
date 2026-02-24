package com.company.ppm.dto;

import com.company.ppm.domain.enums.RoleName;
import java.util.Set;

public record UserResponse(
        Long id,
        String email,
        String fullName,
        boolean active,
        Long organizationId,
        Set<RoleName> roles
) {
}
