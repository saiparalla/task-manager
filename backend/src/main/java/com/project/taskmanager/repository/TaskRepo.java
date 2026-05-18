package com.project.taskmanager.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.project.taskmanager.model.Task;
import com.project.taskmanager.model.UserEntity;

import jakarta.transaction.Transactional;
public interface TaskRepo extends JpaRepository<Task, Long> {
    public Page<Task> findByTitle(String title,Pageable pageable);

    public Page<Task> findAllByUser(UserEntity user, PageRequest of);

    public Page<Task> findByUserAndTitleContaining(UserEntity user, String search, PageRequest of);

    public Optional<Task> findByIdAndUser(Long id, UserEntity user);

    @Transactional
    @Modifying
    public void deleteByIdAndUser(Long id, UserEntity user);
}
