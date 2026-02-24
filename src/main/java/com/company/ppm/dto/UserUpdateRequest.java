package com.company.ppm.dto;

import com.company.ppm.domain.enums.RoleName;
import java.util.Set;

public record UserUpdateRequest(
        String fullName,
        Boolean active,
        Long organizationId,
        Set<RoleName> roles
) {
}
