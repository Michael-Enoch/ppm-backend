package com.company.ppm.service;

import com.company.ppm.common.exception.BadRequestException;
import com.company.ppm.common.exception.ResourceNotFoundException;
import com.company.ppm.domain.entity.Role;
import com.company.ppm.domain.enums.RoleName;
import com.company.ppm.dto.RoleCreateRequest;
import com.company.ppm.dto.RoleResponse;
import com.company.ppm.repository.RoleRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> getAll() {
        return roleRepository.findAll().stream()
                .map(role -> new RoleResponse(role.getId(), role.getName()))
                .toList();
    }

    @Transactional(readOnly = true)
    public RoleResponse getById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + id));
        return new RoleResponse(role.getId(), role.getName());
    }

    @Transactional
    public RoleResponse create(RoleCreateRequest request) {
        if (roleRepository.findByName(request.name()).isPresent()) {
            throw new BadRequestException("Role already exists: " + request.name());
        }
        Role role = new Role();
        role.setName(request.name());
        Role saved = roleRepository.save(role);
        return new RoleResponse(saved.getId(), saved.getName());
    }

    @Transactional
    public void delete(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + id));
        roleRepository.delete(role);
    }

    @Transactional(readOnly = true)
    public Role requireRole(RoleName roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName));
    }
}
