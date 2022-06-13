package com.blackswan.tasks.controller;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import com.blackswan.tasks.api.IdResponse;
import com.blackswan.tasks.api.TaskRequest;
import com.blackswan.tasks.api.TaskResponse;
import com.blackswan.tasks.service.TaskService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user/{userId}/task")
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Slf4j
public class TaskController {

    TaskService taskService;

    @PostMapping
    @ResponseStatus(CREATED)
    public IdResponse createTask(@PathVariable Long userId, @Valid @RequestBody TaskRequest taskRequest) {
        return new IdResponse(taskService.createNewTask(userId, taskRequest));
    }

    @PutMapping("/{taskId}")
    @ResponseStatus(ACCEPTED)
    public String updateTask(@PathVariable Long userId, @PathVariable Long taskId, @RequestBody TaskRequest taskRequest) {
        taskService.updateTask(userId, taskId, taskRequest);
        return "OK";
    }

    @DeleteMapping("/{taskId}")
    @ResponseStatus(OK)
    public TaskResponse deleteTask(@PathVariable Long userId, @PathVariable Long taskId) {
        return taskService.deleteTask(taskId, userId);
    }

    @GetMapping("/{taskId}")
    @ResponseStatus(OK)
    public TaskResponse findTask(@PathVariable Long userId, @PathVariable Long taskId) {
        return taskService.findTask(userId, taskId).orElseThrow();
    }

    @GetMapping
    @ResponseStatus(OK)
    public List<TaskResponse> listTasks(@PathVariable Long userId) {
        return taskService.findAll(userId);
    }

}
