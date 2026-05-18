package com.project.taskmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.project.taskmanager.model.UserEntity;
import java.util.Optional;


public interface UserRepo extends JpaRepository<UserEntity,Long>{
    Optional<UserEntity> findByUserName(String userName);
    Optional<UserEntity> findByEmail(String userName);
}
