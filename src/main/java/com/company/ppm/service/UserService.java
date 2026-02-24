package com.company.ppm.service;

import com.company.ppm.common.exception.BadRequestException;
import com.company.ppm.common.exception.ResourceNotFoundException;
import com.company.ppm.domain.entity.AppUser;
import com.company.ppm.domain.entity.Role;
import com.company.ppm.domain.enums.RoleName;
import com.company.ppm.dto.UserCreateRequest;
import com.company.ppm.dto.UserResponse;
import com.company.ppm.dto.UserUpdateRequest;
import com.company.ppm.repository.UserRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final OrganizationService organizationService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            OrganizationService organizationService,
            RoleService roleService,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.organizationService = organizationService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        return toResponse(requireUser(id));
    }

    @Transactional
    public UserResponse create(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email already exists");
        }

        AppUser user = new AppUser();
        user.setEmail(request.email().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setFullName(request.fullName());
        user.setActive(true);
        user.setOrganization(organizationService.requireOrganization(request.organizationId()));
        user.setRoles(resolveRoles(request.roles()));

        return toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse update(Long id, UserUpdateRequest request) {
        AppUser user = requireUser(id);

        if (request.fullName() != null && !request.fullName().isBlank()) {
            user.setFullName(request.fullName());
        }
        if (request.active() != null) {
            user.setActive(request.active());
        }
        if (request.organizationId() != null) {
            user.setOrganization(organizationService.requireOrganization(request.organizationId()));
        }
        if (request.roles() != null && !request.roles().isEmpty()) {
            user.setRoles(resolveRoles(request.roles()));
        }

        return toResponse(userRepository.save(user));
    }

    @Transactional
    public void delete(Long id) {
        userRepository.delete(requireUser(id));
    }

    @Transactional(readOnly = true)
    public AppUser requireUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    private Set<Role> resolveRoles(Set<RoleName> roleNames) {
        Set<Role> roles = new HashSet<>();
        for (RoleName roleName : roleNames) {
            roles.add(roleService.requireRole(roleName));
        }
        return roles;
    }

    private UserResponse toResponse(AppUser user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.isActive(),
                user.getOrganization().getId(),
                user.getRoles().stream().map(Role::getName).collect(java.util.stream.Collectors.toSet())
        );
    }
}
