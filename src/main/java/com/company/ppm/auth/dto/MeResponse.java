package com.company.ppm.auth.dto;

import com.company.ppm.domain.enums.RoleName;
import java.util.Set;

public record MeResponse(
        Long id,
        String email,
        String fullName,
        boolean active,
        Long organizationId,
        Set<RoleName> roles
) {
}
