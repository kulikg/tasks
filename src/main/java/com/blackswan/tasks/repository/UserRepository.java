package com.blackswan.tasks.repository;

import com.blackswan.tasks.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

}
