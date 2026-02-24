package com.company.ppm.dto;

import com.company.ppm.domain.enums.RoleName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

public record UserCreateRequest(
        @NotBlank @Email String email,
        @NotBlank String password,
        @NotBlank String fullName,
        @NotNull Long organizationId,
        @NotEmpty Set<RoleName> roles
) {
}
