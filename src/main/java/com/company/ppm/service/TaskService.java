package com.company.ppm.service;

import com.company.ppm.common.exception.BadRequestException;
import com.company.ppm.common.exception.ResourceNotFoundException;
import com.company.ppm.domain.entity.AppUser;
import com.company.ppm.domain.entity.Task;
import com.company.ppm.domain.enums.TaskPriority;
import com.company.ppm.domain.enums.TaskStatus;
import com.company.ppm.dto.TaskCreateRequest;
import com.company.ppm.dto.TaskCommentRequest;
import com.company.ppm.dto.TaskOrderRequest;
import com.company.ppm.dto.TaskResponse;
import com.company.ppm.dto.TaskUpdateRequest;
import com.company.ppm.mapper.TaskMapper;
import com.company.ppm.repository.TaskRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectService projectService;
    private final UserService userService;
    private final TaskMapper taskMapper;
    private final TaskEventPublisher taskEventPublisher;
    private final CurrentUserService currentUserService;

    public TaskService(
            TaskRepository taskRepository,
            ProjectService projectService,
            UserService userService,
            TaskMapper taskMapper,
            TaskEventPublisher taskEventPublisher,
            CurrentUserService currentUserService
    ) {
        this.taskRepository = taskRepository;
        this.projectService = projectService;
        this.userService = userService;
        this.taskMapper = taskMapper;
        this.taskEventPublisher = taskEventPublisher;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public TaskResponse createTask(Long projectId, TaskCreateRequest request) {
        var project = projectService.requireProjectAccessible(projectId);
        List<Task> existingTasks = taskRepository.findByProjectIdOrderByOrderIndexAsc(projectId);

        Task task = new Task();
        task.setProject(project);
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(request.status() == null ? TaskStatus.TODO : request.status());
        task.setPriority(request.priority() == null ? TaskPriority.MEDIUM : request.priority());
        task.setDueDate(request.dueDate());
        task.setOrderIndex(existingTasks.size());

        if (request.assigneeId() != null) {
            AppUser assignee = userService.requireUser(request.assigneeId());
            task.setAssignee(assignee);
        }

        Task saved = taskRepository.save(task);
        taskEventPublisher.taskCreated(saved, currentUserService.currentUser().getEmail());
        return taskMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> listTasks(Long projectId) {
        projectService.requireProjectAccessible(projectId);
        return taskRepository.findByProjectIdOrderByOrderIndexAsc(projectId).stream()
                .map(taskMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaskResponse getTask(Long projectId, Long taskId) {
        projectService.requireProjectAccessible(projectId);
        Task task = taskRepository.findByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskId));
        return taskMapper.toResponse(task);
    }

    @Transactional
    public TaskResponse updateTask(Long projectId, Long taskId, TaskUpdateRequest request) {
        projectService.requireProjectAccessible(projectId);
        Task task = taskRepository.findByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskId));

        if (request.title() != null) {
            task.setTitle(request.title());
        }
        if (request.description() != null) {
            task.setDescription(request.description());
        }
        if (request.status() != null) {
            task.setStatus(request.status());
        }
        if (request.priority() != null) {
            task.setPriority(request.priority());
        }
        if (request.dueDate() != null) {
            task.setDueDate(request.dueDate());
        }
        if (request.assigneeId() != null) {
            task.setAssignee(userService.requireUser(request.assigneeId()));
        }

        Task saved = taskRepository.save(task);
        taskEventPublisher.taskUpdated(saved, currentUserService.currentUser().getEmail());
        return taskMapper.toResponse(saved);
    }

    @Transactional
    public List<TaskResponse> reorder(Long projectId, TaskOrderRequest request) {
        projectService.requireProjectAccessible(projectId);

        List<Task> current = taskRepository.findByProjectIdOrderByOrderIndexAsc(projectId);
        Map<Long, Task> byId = new HashMap<>();
        for (Task task : current) {
            byId.put(task.getId(), task);
        }

        Set<Long> inputIds = new HashSet<>(request.taskIds());
        Set<Long> existingIds = byId.keySet();
        if (request.taskIds().size() != current.size() || inputIds.size() != request.taskIds().size() || !inputIds.equals(existingIds)) {
            throw new BadRequestException("Task order payload must include each task ID exactly once");
        }

        List<Task> reordered = new ArrayList<>();
        for (int i = 0; i < request.taskIds().size(); i++) {
            Long taskId = request.taskIds().get(i);
            Task task = byId.get(taskId);
            if (task == null) {
                throw new BadRequestException("Unknown task id in ordering: " + taskId);
            }
            task.setOrderIndex(i);
            reordered.add(task);
        }

        return taskRepository.saveAll(reordered).stream().map(taskMapper::toResponse).toList();
    }

    @Transactional
    public void addComment(Long projectId, Long taskId, TaskCommentRequest request) {
        projectService.requireProjectAccessible(projectId);
        Task task = taskRepository.findByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskId));
        taskEventPublisher.taskCommented(task, currentUserService.currentUser().getEmail(), request.comment());
    }

    @Transactional
    public void deleteTask(Long projectId, Long taskId) {
        projectService.requireProjectAccessible(projectId);
        Task task = taskRepository.findByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskId));
        taskRepository.delete(task);
    }
}
