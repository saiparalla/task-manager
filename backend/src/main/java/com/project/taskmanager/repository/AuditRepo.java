package com.project.taskmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.taskmanager.model.AuditLog;

public interface AuditRepo extends JpaRepository<AuditLog,Long>{
    
    
}
