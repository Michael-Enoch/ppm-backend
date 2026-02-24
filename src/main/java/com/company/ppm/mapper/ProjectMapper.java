package com.company.ppm.mapper;

import com.company.ppm.domain.entity.Project;
import com.company.ppm.dto.ProjectResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mapping(target = "organizationId", source = "organization.id")
    @Mapping(target = "createdBy", source = "createdBy.id")
    ProjectResponse toResponse(Project project);
}
