package com.company.ppm.service;

import com.company.ppm.common.exception.ResourceNotFoundException;
import com.company.ppm.domain.entity.Organization;
import com.company.ppm.dto.OrganizationRequest;
import com.company.ppm.dto.OrganizationResponse;
import com.company.ppm.repository.OrganizationRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    public OrganizationService(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @Transactional(readOnly = true)
    public List<OrganizationResponse> findAll() {
        return organizationRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrganizationResponse findById(Long id) {
        return toResponse(requireOrganization(id));
    }

    @Transactional
    public OrganizationResponse create(OrganizationRequest request) {
        Organization organization = new Organization();
        organization.setName(request.name());
        organization.setCode(request.code());
        return toResponse(organizationRepository.save(organization));
    }

    @Transactional
    public OrganizationResponse update(Long id, OrganizationRequest request) {
        Organization organization = requireOrganization(id);
        organization.setName(request.name());
        organization.setCode(request.code());
        return toResponse(organizationRepository.save(organization));
    }

    @Transactional
    public void delete(Long id) {
        organizationRepository.delete(requireOrganization(id));
    }

    @Transactional(readOnly = true)
    public Organization requireOrganization(Long id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found: " + id));
    }

    private OrganizationResponse toResponse(Organization organization) {
        return new OrganizationResponse(
                organization.getId(),
                organization.getName(),
                organization.getCode(),
                organization.getCreatedAt(),
                organization.getUpdatedAt()
        );
    }
}
