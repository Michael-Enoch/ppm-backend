package com.company.ppm.events;

import com.company.ppm.domain.enums.TaskEventType;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TaskDomainEvent {
    private TaskEventType type;
    private Long taskId;
    private Long projectId;
    private String actor;
    private String message;
    private Instant occurredAt;
}
