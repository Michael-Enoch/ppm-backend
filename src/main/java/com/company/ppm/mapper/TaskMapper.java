package com.company.ppm.mapper;

import com.company.ppm.domain.entity.Task;
import com.company.ppm.dto.TaskResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "assigneeId", source = "assignee.id")
    TaskResponse toResponse(Task task);
}
