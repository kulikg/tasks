package com.blackswan.tasks;

import static java.util.concurrent.Executors.newScheduledThreadPool;
import static java.util.concurrent.TimeUnit.SECONDS;
import static lombok.AccessLevel.PRIVATE;

import com.blackswan.tasks.service.TaskService;
import java.util.concurrent.ScheduledExecutorService;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = PRIVATE)
@Slf4j
public class TaskScheduler {

    final ScheduledExecutorService scheduler = newScheduledThreadPool(1);
    @Value("${scheduleRateInSeconds:0}")
    int scheduleRateInSeconds;
    @Autowired
    TaskService taskService;

    @PostConstruct
    public void startScheduler() {
        if (scheduleRateInSeconds > 0) {
            scheduler.scheduleAtFixedRate(this::trigger, scheduleRateInSeconds, scheduleRateInSeconds, SECONDS);
        }
    }

    @PreDestroy
    public void stopScheduler() throws Exception {
        scheduler.shutdown();
        scheduler.awaitTermination(10, SECONDS);
    }

    private void trigger() {
        while (true) {
            val taskOption = taskService.taskToSchedule();
            taskOption.ifPresent(task -> log.info("will schedule task {}", task.getName()));
            if (taskOption.isEmpty()) {
                break;
            }
        }
    }

}
