package com.company.ppm.controller;

import com.company.ppm.dto.RoleCreateRequest;
import com.company.ppm.dto.RoleResponse;
import jakarta.validation.Valid;
import com.company.ppm.service.RoleService;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER')")
    public List<RoleResponse> listRoles() {
        return roleService.getAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER')")
    public RoleResponse getRole(@PathVariable Long id) {
        return roleService.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public RoleResponse createRole(@Valid @RequestBody RoleCreateRequest request) {
        return roleService.create(request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteRole(@PathVariable Long id) {
        roleService.delete(id);
    }
}
