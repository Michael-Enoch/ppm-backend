package com.company.ppm.auth;

import com.company.ppm.auth.dto.LoginRequest;
import com.company.ppm.auth.dto.LogoutRequest;
import com.company.ppm.auth.dto.RefreshRequest;
import com.company.ppm.auth.dto.TokenResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
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
}
