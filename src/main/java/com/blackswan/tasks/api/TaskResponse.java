package com.blackswan.tasks.api;

import static lombok.AccessLevel.PRIVATE;

import com.blackswan.tasks.domain.TaskStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Builder(toBuilder = true)
@FieldDefaults(level = PRIVATE)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class TaskResponse {
    Long id;
    String name;
    String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("date_time")
    LocalDateTime dateTime;
    TaskStatus status;
    String owner;
}
