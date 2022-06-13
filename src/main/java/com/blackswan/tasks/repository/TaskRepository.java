package com.blackswan.tasks.repository;

import com.blackswan.tasks.domain.TaskEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    Optional<TaskEntity> findByIdAndOwnerId(Long id, Long userId);
    List<TaskEntity> findByOwnerId(Long userId);

}
