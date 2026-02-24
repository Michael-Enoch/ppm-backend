package com.company.ppm.service;

import com.company.ppm.common.exception.BadRequestException;
import com.company.ppm.common.exception.ResourceNotFoundException;
import com.company.ppm.domain.entity.AppUser;
import com.company.ppm.domain.entity.Kpi;
import com.company.ppm.domain.entity.Organization;
import com.company.ppm.dto.KpiCreateRequest;
import com.company.ppm.dto.KpiResponse;
import com.company.ppm.dto.KpiUpdateRequest;
import com.company.ppm.mapper.KpiMapper;
import com.company.ppm.repository.KpiRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KpiService {

    private final KpiRepository kpiRepository;
    private final OrganizationService organizationService;
    private final CurrentUserService currentUserService;
    private final KpiMapper kpiMapper;

    public KpiService(
            KpiRepository kpiRepository,
            OrganizationService organizationService,
            CurrentUserService currentUserService,
            KpiMapper kpiMapper
    ) {
        this.kpiRepository = kpiRepository;
        this.organizationService = organizationService;
        this.currentUserService = currentUserService;
        this.kpiMapper = kpiMapper;
    }

    @Transactional(readOnly = true)
    public List<KpiResponse> list() {
        if (currentUserService.isAdmin()) {
            return kpiRepository.findAll().stream().map(kpiMapper::toResponse).toList();
        }

        AppUser user = currentUserService.currentUser();
        return kpiRepository.findByOrganizationId(user.getOrganization().getId()).stream()
                .map(kpiMapper::toResponse)
                .toList();
    }

    @Transactional
    public KpiResponse create(KpiCreateRequest request) {
        AppUser actor = currentUserService.currentUser();
        Organization organization = organizationService.requireOrganization(request.organizationId());

        if (!currentUserService.isAdmin() && !organization.getId().equals(actor.getOrganization().getId())) {
            throw new BadRequestException("Cannot create KPI in another organization");
        }

        Kpi kpi = new Kpi();
        kpi.setName(request.name());
        kpi.setDescription(request.description());
        kpi.setUnit(request.unit());
        kpi.setTargetValue(request.targetValue());
        kpi.setOrganization(organization);

        return kpiMapper.toResponse(kpiRepository.save(kpi));
    }

    @Transactional(readOnly = true)
    public KpiResponse get(Long kpiId) {
        return kpiMapper.toResponse(requireAccessible(kpiId));
    }

    @Transactional
    public KpiResponse update(Long kpiId, KpiUpdateRequest request) {
        Kpi kpi = requireAccessible(kpiId);
        if (request.name() != null && !request.name().isBlank()) {
            kpi.setName(request.name());
        }
        if (request.description() != null) {
            kpi.setDescription(request.description());
        }
        if (request.unit() != null && !request.unit().isBlank()) {
            kpi.setUnit(request.unit());
        }
        if (request.targetValue() != null) {
            kpi.setTargetValue(request.targetValue());
        }
        return kpiMapper.toResponse(kpiRepository.save(kpi));
    }

    @Transactional
    public void delete(Long kpiId) {
        kpiRepository.delete(requireAccessible(kpiId));
    }

    @Transactional(readOnly = true)
    public Kpi requireAccessible(Long kpiId) {
        Kpi kpi = kpiRepository.findById(kpiId)
                .orElseThrow(() -> new ResourceNotFoundException("KPI not found: " + kpiId));

        if (currentUserService.isAdmin()) {
            return kpi;
        }

        AppUser user = currentUserService.currentUser();
        if (!kpi.getOrganization().getId().equals(user.getOrganization().getId())) {
            throw new ResourceNotFoundException("KPI not found: " + kpiId);
        }
        return kpi;
    }
}
