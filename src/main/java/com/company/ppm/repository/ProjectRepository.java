package com.company.ppm.repository;

import com.company.ppm.domain.entity.Project;
import com.company.ppm.domain.enums.ProjectStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByOrganizationId(Long organizationId);
    long countByStatus(ProjectStatus status);
}
