package com.company.ppm.service;

import com.company.ppm.common.exception.UnauthorizedException;
import com.company.ppm.domain.entity.AppUser;
import com.company.ppm.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AppUser currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new UnauthorizedException("No authenticated user");
        }

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UnauthorizedException("Authenticated user not found"));
    }

    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
    }
}
