package com.company.ppm.service;

import com.company.ppm.domain.enums.ProjectStatus;
import com.company.ppm.domain.enums.TaskStatus;
import com.company.ppm.dto.DashboardResponse;
import com.company.ppm.repository.KpiRepository;
import com.company.ppm.repository.ProjectRepository;
import com.company.ppm.repository.TaskRepository;
import java.time.Instant;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final KpiRepository kpiRepository;
    private final CurrentUserService currentUserService;

    public DashboardService(
            ProjectRepository projectRepository,
            TaskRepository taskRepository,
            KpiRepository kpiRepository,
            CurrentUserService currentUserService
    ) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.kpiRepository = kpiRepository;
        this.currentUserService = currentUserService;
    }

    @Cacheable("dashboard")
    @Transactional(readOnly = true)
    public DashboardResponse getDashboard() {
        if (currentUserService.isAdmin()) {
            return new DashboardResponse(
                    projectRepository.count(),
                    projectRepository.countByStatus(ProjectStatus.ACTIVE),
                    taskRepository.count(),
                    taskRepository.countByStatus(TaskStatus.DONE),
                    kpiRepository.count(),
                    Instant.now()
            );
        }

        Long organizationId = currentUserService.currentUser().getOrganization().getId();
        var projects = projectRepository.findByOrganizationId(organizationId);
        long activeProjects = projects.stream().filter(project -> project.getStatus() == ProjectStatus.ACTIVE).count();

        long totalTasks = 0;
        long completedTasks = 0;
        for (var project : projects) {
            var tasks = taskRepository.findByProjectIdOrderByOrderIndexAsc(project.getId());
            totalTasks += tasks.size();
            completedTasks += tasks.stream().filter(task -> task.getStatus() == TaskStatus.DONE).count();
        }

        return new DashboardResponse(
                projects.size(),
                activeProjects,
                totalTasks,
                completedTasks,
                kpiRepository.findByOrganizationId(organizationId).size(),
                Instant.now()
        );
    }
}
