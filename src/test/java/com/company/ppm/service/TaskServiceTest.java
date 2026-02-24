package com.company.ppm.service;

import com.company.ppm.common.exception.BadRequestException;
import com.company.ppm.domain.entity.AppUser;
import com.company.ppm.domain.entity.Organization;
import com.company.ppm.domain.entity.Project;
import com.company.ppm.domain.entity.Task;
import com.company.ppm.domain.enums.TaskPriority;
import com.company.ppm.domain.enums.TaskStatus;
import com.company.ppm.dto.TaskCreateRequest;
import com.company.ppm.dto.TaskOrderRequest;
import com.company.ppm.dto.TaskResponse;
import com.company.ppm.mapper.TaskMapper;
import com.company.ppm.repository.TaskRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectService projectService;

    @Mock
    private UserService userService;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private TaskEventPublisher taskEventPublisher;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private TaskService taskService;

    @Test
    void createTaskAssignsNextOrderIndex() {
        Project project = new Project();
        project.setId(10L);
        project.setOrganization(new Organization());

        Task existingA = new Task();
        existingA.setId(1L);
        existingA.setOrderIndex(0);

        Task existingB = new Task();
        existingB.setId(2L);
        existingB.setOrderIndex(1);

        AppUser actor = new AppUser();
        actor.setEmail("pm@example.com");

        Task saved = new Task();
        saved.setId(99L);
        saved.setProject(project);
        saved.setStatus(TaskStatus.TODO);
        saved.setPriority(TaskPriority.MEDIUM);

        TaskResponse mapped = new TaskResponse(
                99L,
                10L,
                "Implement API",
                "desc",
                "TODO",
                "MEDIUM",
                null,
                2,
                LocalDate.of(2026, 2, 24),
                null,
                null
        );

        when(projectService.requireProjectAccessible(10L)).thenReturn(project);
        when(taskRepository.findByProjectIdOrderByOrderIndexAsc(10L)).thenReturn(List.of(existingA, existingB));
        when(taskRepository.save(any(Task.class))).thenReturn(saved);
        when(taskMapper.toResponse(saved)).thenReturn(mapped);
        when(currentUserService.currentUser()).thenReturn(actor);

        TaskResponse response = taskService.createTask(
                10L,
                new TaskCreateRequest(
                        "Implement API",
                        "desc",
                        null,
                        null,
                        null,
                        LocalDate.of(2026, 2, 24)
                )
        );

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());
        assertThat(captor.getValue().getOrderIndex()).isEqualTo(2);
        assertThat(response.id()).isEqualTo(99L);
    }

    @Test
    void reorderRejectsMissingTaskIds() {
        Project project = new Project();
        project.setId(1L);

        Task task = new Task();
        task.setId(11L);
        task.setProject(project);

        when(projectService.requireProjectAccessible(1L)).thenReturn(project);
        when(taskRepository.findByProjectIdOrderByOrderIndexAsc(1L)).thenReturn(List.of(task));

        assertThatThrownBy(() -> taskService.reorder(1L, new TaskOrderRequest(List.of())))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("include each task ID exactly once");
    }
}
