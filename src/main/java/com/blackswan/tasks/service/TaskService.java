package com.blackswan.tasks.service;

import static com.blackswan.tasks.domain.TaskStatus.DONE;
import static com.blackswan.tasks.domain.TaskStatus.PENDING;
import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.blackswan.tasks.api.TaskRequest;
import com.blackswan.tasks.api.TaskResponse;
import com.blackswan.tasks.domain.TaskEntity;
import com.blackswan.tasks.repository.TaskRepository;
import com.blackswan.tasks.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class TaskService {

    TaskRepository taskRepository;
    UserRepository userRepository;
    EntityManager entityManager;

    @Transactional
    public Long createNewTask(Long userId, TaskRequest taskRequest) {
        val owner = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "user does not exist"));
        val taskEntity = TaskEntity.builder()
                .owner(owner)
                .dateTime(taskRequest.getDateTime())
                .name(taskRequest.getName())
                .description(taskRequest.getDescription())
                .build();
        return taskRepository.save(taskEntity).getId();
    }

    @Transactional
    public void updateTask(Long userId, Long taskId, TaskRequest taskRequest) {
        val existingTask = taskRepository.findByIdAndOwnerId(taskId, userId)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "could not find task"));
        val ongoingTaskUpdate = existingTask.toBuilder();
        Optional.ofNullable(taskRequest.getDateTime()).ifPresent(newDate -> {
                    if (taskRequest.getDateTime().isBefore(LocalDateTime.now().plusSeconds(1))) {
                        throw new ResponseStatusException(BAD_REQUEST, "not allowed to move task back in time");
                    }
                    if (DONE.equals(existingTask.getStatus())) {
                        throw new ResponseStatusException(BAD_REQUEST, "Task is already finished");
                    }
                    ongoingTaskUpdate.dateTime(taskRequest.getDateTime());
                }
        );
        Optional.ofNullable(taskRequest.getName()).ifPresent(ongoingTaskUpdate::name);
        Optional.ofNullable(taskRequest.getDescription()).ifPresent(ongoingTaskUpdate::description);
        taskRepository.save(ongoingTaskUpdate.build());
    }

    @Transactional
    public Optional<TaskResponse> findTask(Long userId, Long taskId) {
        return taskRepository.findByIdAndOwnerId(taskId, userId)
                .map(this::translateEntityToResponse);
    }

    @Transactional
    public List<TaskResponse> findAll(Long userId) {
        return taskRepository.findByOwnerId(userId)
                .stream()
                .map(this::translateEntityToResponse)
                .collect(toList());
    }

    @Transactional
    public Optional<TaskEntity> taskToSchedule() {
        val builder = entityManager.getCriteriaBuilder();
        val query = builder.createQuery(TaskEntity.class);
        val root = query.from(TaskEntity.class);
        val statusCriteria = builder.equal(root.get("status"), PENDING);
        val timeCriteria = builder.greaterThan(root.get("dateTime"), builder.currentTime());
        query.select(root).where(builder.and(statusCriteria, timeCriteria));
        val result = entityManager.createQuery(query).setMaxResults(1).getResultList().stream().findFirst();
        result.ifPresent(task -> taskRepository.save(task.toBuilder().status(DONE).build()));
        return result;
    }

    @Transactional
    public TaskResponse deleteTask(Long taskId, Long ownerId) {
        val entity = taskRepository.findByIdAndOwnerId(taskId, ownerId)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "task not found"));
        val task = translateEntityToResponse(entity);
        taskRepository.delete(entity);
        return task;
    }

    private TaskResponse translateEntityToResponse(TaskEntity entity) {
        return TaskResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .dateTime(entity.getDateTime())
                .owner(entity.getOwner().getUserName())
                .build();
    }
}
