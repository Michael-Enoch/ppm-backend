package com.company.ppm.service;

import com.company.ppm.config.AppProperties;
import com.company.ppm.domain.entity.Task;
import com.company.ppm.domain.enums.TaskEventType;
import com.company.ppm.events.TaskDomainEvent;
import java.time.Instant;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class TaskEventPublisher {

    private final SimpMessagingTemplate messagingTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final AppProperties appProperties;

    public TaskEventPublisher(
            SimpMessagingTemplate messagingTemplate,
            KafkaTemplate<String, Object> kafkaTemplate,
            AppProperties appProperties
    ) {
        this.messagingTemplate = messagingTemplate;
        this.kafkaTemplate = kafkaTemplate;
        this.appProperties = appProperties;
    }

    public void taskCreated(Task task, String actor) {
        publish(task, actor, TaskEventType.TASK_CREATED, "Task created");
    }

    public void taskUpdated(Task task, String actor) {
        publish(task, actor, TaskEventType.TASK_UPDATED, "Task updated");
    }

    public void taskCommented(Task task, String actor, String comment) {
        publish(task, actor, TaskEventType.TASK_COMMENTED, comment);
    }

    private void publish(Task task, String actor, TaskEventType type, String message) {
        TaskDomainEvent event = new TaskDomainEvent();
        event.setType(type);
        event.setTaskId(task.getId());
        event.setProjectId(task.getProject().getId());
        event.setActor(actor);
        event.setMessage(message);
        event.setOccurredAt(Instant.now());

        messagingTemplate.convertAndSend("/topic/projects/" + task.getProject().getId() + "/tasks", event);
        kafkaTemplate.send(appProperties.getKafka().getTopics().getTaskEvents(), String.valueOf(task.getId()), event);
    }
}
