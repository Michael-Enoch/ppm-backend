package com.company.ppm.auth;

import com.company.ppm.auth.dto.LoginRequest;
import com.company.ppm.auth.dto.LogoutRequest;
import com.company.ppm.auth.dto.MeResponse;
import com.company.ppm.auth.dto.RefreshRequest;
import com.company.ppm.auth.dto.TokenResponse;
import com.company.ppm.domain.entity.Role;
import com.company.ppm.domain.enums.RoleName;
import com.company.ppm.service.CurrentUserService;
import jakarta.validation.Valid;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final CurrentUserService currentUserService;

    public AuthController(AuthService authService, CurrentUserService currentUserService) {
        this.authService = authService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public TokenResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return authService.refresh(request.refreshToken());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody(required = false) LogoutRequest request, Authentication authentication) {
        String principal = authentication == null ? null : authentication.getName();
        String refreshToken = request == null ? null : request.refreshToken();
        authService.logout(refreshToken, principal);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public MeResponse me() {
        var user = currentUserService.currentUser();
        Set<RoleName> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return new MeResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.isActive(),
                user.getOrganization().getId(),
                roles
        );
    }
}
