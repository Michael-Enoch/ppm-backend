package com.company.ppm.controller;

import com.company.ppm.dto.OrganizationRequest;
import com.company.ppm.dto.OrganizationResponse;
import com.company.ppm.service.OrganizationService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER')")
    public List<OrganizationResponse> listOrganizations() {
        return organizationService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER')")
    public OrganizationResponse getOrganization(@PathVariable Long id) {
        return organizationService.findById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public OrganizationResponse createOrganization(@Valid @RequestBody OrganizationRequest request) {
        return organizationService.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public OrganizationResponse updateOrganization(@PathVariable Long id, @Valid @RequestBody OrganizationRequest request) {
        return organizationService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteOrganization(@PathVariable Long id) {
        organizationService.delete(id);
    }
}
