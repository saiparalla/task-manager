package com.project.taskmanager.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.taskmanager.model.OtpEntity;

public interface OtpRepo extends JpaRepository<OtpEntity,Long>{
    Optional<OtpEntity> findTopByEmailOrderByCreatedAtDesc(String email);
}