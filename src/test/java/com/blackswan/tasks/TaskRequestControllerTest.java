package com.blackswan.tasks;

import static com.blackswan.tasks.Fixtures.aTaskRequest;
import static com.blackswan.tasks.Fixtures.aUserRequest;
import static com.blackswan.tasks.Fixtures.otherTaskRequest;
import static com.blackswan.tasks.domain.TaskStatus.PENDING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;

import com.blackswan.tasks.api.TaskRequest;
import com.blackswan.tasks.api.TaskResponse;
import java.time.LocalDateTime;
import java.util.List;
import lombok.val;
import org.junit.jupiter.api.Test;

public class TaskRequestControllerTest extends AbstractTest {

    @Test
    void createTask() {
        val userId = post("api/user", aUserRequest).getBody().getId();
        val status = post(pathForUser(userId), aTaskRequest).getStatusCode();
        assertThat(status).isEqualTo(CREATED);
    }

    @Test
    void listTasks() {
        val userId = post("api/user", aUserRequest).getBody().getId();
        val taskId = post(pathForUser(userId), aTaskRequest).getBody().getId();
        val otherTaskId = post(pathForUser(userId), otherTaskRequest).getBody().getId();
        val actual = get(pathForUser(userId), TaskResponse[].class).getBody();
        val expected = List.of(
                TaskResponse.builder()
                        .id(taskId)
                        .name(aTaskRequest.getName())
                        .description(aTaskRequest.getDescription())
                        .dateTime(aTaskRequest.getDateTime())
                        .status(PENDING)
                        .owner(aUserRequest.getUserName())
                        .build(),
                TaskResponse.builder()
                        .id(otherTaskId)
                        .name(otherTaskRequest.getName())
                        .description(otherTaskRequest.getDescription())
                        .dateTime(otherTaskRequest.getDateTime())
                        .status(PENDING)
                        .owner(aUserRequest.getUserName())
                        .build()
        );
        assertThat(actual).hasSameElementsAs(expected);
    }

    @Test
    void updateTask() {
        val userId = post("api/user", aUserRequest).getBody().getId();
        val taskId = post(pathForUser(userId), aTaskRequest).getBody().getId();
        val taskUpdate = TaskRequest.builder().description("changed").build();
        val pathForTask = pathForUser(userId) + "/" + taskId;
        put(pathForTask, taskUpdate);
        val actual = get(pathForTask, TaskResponse.class).getBody().getDescription();
        assertThat(actual).isEqualTo("changed");
    }

    @Test
    void updateTaskWithExpiredDateTime() {
        val userId = post("api/user", aUserRequest).getBody().getId();
        val taskId = post(pathForUser(userId), aTaskRequest).getBody().getId();
        val taskUpdate = TaskRequest.builder().dateTime(LocalDateTime.now().minusDays(1)).build();
        val pathForTask = pathForUser(userId) + "/" + taskId;
        val actual = put(pathForTask, taskUpdate).getStatusCode();
        assertThat(actual).isEqualTo(BAD_REQUEST);
    }

    @Test
    void deleteTask() {
        val userId = post("api/user", aUserRequest).getBody().getId();
        val taskId = post(pathForUser(userId), aTaskRequest).getBody();
        delete(pathForUser(userId) + "/" + taskId);
    }

    @Test
    void createAlreadyExpiredTask() {
        val userId = post("api/user", aUserRequest).getBody().getId();
        val actual = post(pathForUser(userId), aTaskRequest.toBuilder()
                .dateTime(LocalDateTime.now().minusDays(1))
                .build())
                .getStatusCode();
        assertThat(actual).isEqualTo(BAD_REQUEST);
    }

    @Test
    void createTaskWithoutUser() {
        val actual = post(pathForUser(-1L), aTaskRequest).getStatusCode();
        assertThat(actual).isEqualTo(BAD_REQUEST);
    }

    @Test
    void createTaskWithoutName() {
        val userId = post("api/user", aUserRequest).getBody().getId();
        val actual = post(pathForUser(userId),
                aTaskRequest.toBuilder().name("").build()).getStatusCode();
        assertThat(actual).isEqualTo(BAD_REQUEST);
    }

    @Test
    void createTaskWithoutDateTime() {
        val userId = post("api/user", aUserRequest).getBody().getId();
        val actual = post(pathForUser(userId),
                aTaskRequest.toBuilder().dateTime(null).build()).getStatusCode();
        assertThat(actual).isEqualTo(BAD_REQUEST);
    }

    @Test
    void updateTaskWithWrongUser() {
        val userId = post("api/user", aUserRequest).getBody().getId();
        val taskId = post(pathForUser(userId), aTaskRequest).getBody().getId();
        val updatedTask = aTaskRequest.toBuilder().description("changed").build();
        val pathForTask = pathForUser(-1L) + "/" + taskId;
        val actual = put(pathForTask, updatedTask).getStatusCode();
        assertThat(actual).isEqualTo(BAD_REQUEST);
    }

}
