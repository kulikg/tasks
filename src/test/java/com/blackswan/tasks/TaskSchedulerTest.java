package com.blackswan.tasks;

import static com.blackswan.tasks.Fixtures.aTaskRequest;
import static com.blackswan.tasks.Fixtures.aUserRequest;
import static com.blackswan.tasks.domain.TaskStatus.DONE;
import static java.time.Duration.ofSeconds;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

import com.blackswan.tasks.api.TaskResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.val;
import org.junit.jupiter.api.Test;

class TaskSchedulerTest extends AbstractTest {

    @Override
    protected Map<String, String> appEnv() {
        val env = new HashMap<>(super.appEnv());
        env.put("scheduleRateInSeconds", "1");
        return env;
    }

    @Test
    void shouldScheduleJobs() {
        val userId = post("api/user", aUserRequest).getBody().getId();
        val taskId = post(pathForUser(userId), aTaskRequest).getBody().getId();
        await().atMost(ofSeconds(20))
                .until(() -> Optional.ofNullable(get(pathForUser(userId) + "/" + taskId, TaskResponse.class).getBody())
                        .map(TaskResponse::getStatus)
                        .map(status -> status.equals(DONE))
                        .orElse(false)
                );
    }

}