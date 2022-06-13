package com.blackswan.tasks;

import com.blackswan.tasks.api.TaskRequest;
import com.blackswan.tasks.api.UserRequest;
import java.time.LocalDateTime;

public class Fixtures {

    public static UserRequest aUserRequest = UserRequest.builder()
            .userName("username")
            .firstName("first name")
            .lastName("last name")
            .build();

    public static UserRequest otherUserRequest = UserRequest.builder()
            .userName("other username")
            .firstName("other first name")
            .lastName("other last name")
            .build();

    public static TaskRequest aTaskRequest = TaskRequest.builder()
            .dateTime(LocalDateTime.now().withNano(0))
            .name("task")
            .description("description")
            .build();

    public static TaskRequest otherTaskRequest = TaskRequest.builder()
            .dateTime(LocalDateTime.now().withNano(0))
            .name("other task")
            .description("other description")
            .build();

}
