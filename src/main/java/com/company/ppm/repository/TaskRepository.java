package com.company.ppm.repository;

import com.company.ppm.domain.entity.Task;
import com.company.ppm.domain.enums.TaskStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProjectIdOrderByOrderIndexAsc(Long projectId);
    Optional<Task> findByIdAndProjectId(Long id, Long projectId);
    long countByStatus(TaskStatus status);
}
