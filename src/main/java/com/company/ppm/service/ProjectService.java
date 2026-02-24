package com.company.ppm.service;

import com.company.ppm.common.exception.BadRequestException;
import com.company.ppm.common.exception.ResourceNotFoundException;
import com.company.ppm.domain.entity.AppUser;
import com.company.ppm.domain.entity.Organization;
import com.company.ppm.domain.entity.Project;
import com.company.ppm.domain.enums.ProjectStatus;
import com.company.ppm.dto.ProjectCreateRequest;
import com.company.ppm.dto.ProjectResponse;
import com.company.ppm.dto.ProjectUpdateRequest;
import com.company.ppm.mapper.ProjectMapper;
import com.company.ppm.repository.ProjectRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final CurrentUserService currentUserService;
    private final OrganizationService organizationService;

    public ProjectService(
            ProjectRepository projectRepository,
            ProjectMapper projectMapper,
            CurrentUserService currentUserService,
            OrganizationService organizationService
    ) {
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
        this.currentUserService = currentUserService;
        this.organizationService = organizationService;
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> listProjects() {
        if (currentUserService.isAdmin()) {
            return projectRepository.findAll().stream().map(projectMapper::toResponse).toList();
        }

        AppUser user = currentUserService.currentUser();
        return projectRepository.findByOrganizationId(user.getOrganization().getId()).stream()
                .map(projectMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProject(Long id) {
        return projectMapper.toResponse(requireProjectAccessible(id));
    }

    @Transactional
    public ProjectResponse createProject(ProjectCreateRequest request) {
        AppUser actor = currentUserService.currentUser();
        Organization organization = organizationService.requireOrganization(request.organizationId());

        if (!currentUserService.isAdmin() && !organization.getId().equals(actor.getOrganization().getId())) {
            throw new BadRequestException("Cannot create project in another organization");
        }

        Project project = new Project();
        project.setName(request.name());
        project.setDescription(request.description());
        project.setStatus(request.status() == null ? ProjectStatus.PLANNED : request.status());
        project.setOrganization(organization);
        project.setCreatedBy(actor);

        return projectMapper.toResponse(projectRepository.save(project));
    }

    @Transactional
    public ProjectResponse updateProject(Long projectId, ProjectUpdateRequest request) {
        Project project = requireProjectAccessible(projectId);
        if (request.name() != null && !request.name().isBlank()) {
            project.setName(request.name());
        }
        if (request.description() != null) {
            project.setDescription(request.description());
        }
        if (request.status() != null) {
            project.setStatus(request.status());
        }
        return projectMapper.toResponse(projectRepository.save(project));
    }

    @Transactional
    public void deleteProject(Long projectId) {
        projectRepository.delete(requireProjectAccessible(projectId));
    }

    @Transactional(readOnly = true)
    public Project requireProjectAccessible(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + id));

        if (currentUserService.isAdmin()) {
            return project;
        }

        AppUser user = currentUserService.currentUser();
        if (!project.getOrganization().getId().equals(user.getOrganization().getId())) {
            throw new ResourceNotFoundException("Project not found: " + id);
        }
        return project;
    }
}
