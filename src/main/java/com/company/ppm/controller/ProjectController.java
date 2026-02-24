package com.company.ppm.controller;

import com.company.ppm.dto.ProjectCreateRequest;
import com.company.ppm.dto.ProjectResponse;
import com.company.ppm.dto.ProjectUpdateRequest;
import com.company.ppm.dto.TaskCommentRequest;
import com.company.ppm.dto.TaskCreateRequest;
import com.company.ppm.dto.TaskOrderRequest;
import com.company.ppm.dto.TaskResponse;
import com.company.ppm.dto.TaskUpdateRequest;
import com.company.ppm.service.ProjectService;
import com.company.ppm.service.TaskService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final TaskService taskService;

    public ProjectController(ProjectService projectService, TaskService taskService) {
        this.projectService = projectService;
        this.taskService = taskService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER','PM','MEMBER','VIEWER')")
    public List<ProjectResponse> listProjects() {
        return projectService.listProjects();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER','PM')")
    public ProjectResponse createProject(@Valid @RequestBody ProjectCreateRequest request) {
        return projectService.createProject(request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER','PM','MEMBER','VIEWER')")
    public ProjectResponse getProject(@PathVariable Long id) {
        return projectService.getProject(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER','PM')")
    public ProjectResponse updateProject(@PathVariable Long id, @RequestBody ProjectUpdateRequest request) {
        return projectService.updateProject(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER')")
    public void deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
    }

    @PostMapping("/{id}/tasks")
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER','PM','MEMBER')")
    public TaskResponse createTask(@PathVariable Long id, @Valid @RequestBody TaskCreateRequest request) {
        return taskService.createTask(id, request);
    }

    @PatchMapping("/{id}/tasks/{taskId}")
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER','PM','MEMBER')")
    public TaskResponse updateTask(
            @PathVariable Long id,
            @PathVariable Long taskId,
            @Valid @RequestBody TaskUpdateRequest request
    ) {
        return taskService.updateTask(id, taskId, request);
    }

    @PostMapping("/{id}/tasks/order")
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER','PM')")
    public List<TaskResponse> reorderTasks(@PathVariable Long id, @Valid @RequestBody TaskOrderRequest request) {
        return taskService.reorder(id, request);
    }

    @GetMapping("/{id}/tasks")
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER','PM','MEMBER','VIEWER')")
    public List<TaskResponse> listTasks(@PathVariable Long id) {
        return taskService.listTasks(id);
    }

    @GetMapping("/{id}/tasks/{taskId}")
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER','PM','MEMBER','VIEWER')")
    public TaskResponse getTask(@PathVariable Long id, @PathVariable Long taskId) {
        return taskService.getTask(id, taskId);
    }

    @PostMapping("/{id}/tasks/{taskId}/comments")
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER','PM','MEMBER')")
    public void commentOnTask(
            @PathVariable Long id,
            @PathVariable Long taskId,
            @Valid @RequestBody TaskCommentRequest request
    ) {
        taskService.addComment(id, taskId, request);
    }

    @DeleteMapping("/{id}/tasks/{taskId}")
    @PreAuthorize("hasAnyRole('ADMIN','ORG_MANAGER','PM')")
    public void deleteTask(@PathVariable Long id, @PathVariable Long taskId) {
        taskService.deleteTask(id, taskId);
    }
}
