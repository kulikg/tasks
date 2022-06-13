package com.blackswan.tasks.domain;

import static com.blackswan.tasks.domain.TaskStatus.PENDING;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@FieldDefaults(level = PRIVATE)
@Entity
@Table(name = "tasks")
public class TaskEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    Long id;
    String name;
    String description;
    @Default
    TaskStatus status = PENDING;
    LocalDateTime dateTime;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "owner_id")
    UserEntity owner;

}