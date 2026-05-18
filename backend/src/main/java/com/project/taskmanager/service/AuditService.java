package com.project.taskmanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.taskmanager.model.AuditLog;
import com.project.taskmanager.repository.AuditRepo;

@Service
public class AuditService {

    @Autowired
    private AuditRepo auditRepo;
    
    public void log( String userEmail, String action, String entityType, Long entityId) {
        AuditLog auditLog = new AuditLog();
        auditLog.setUserEmail(userEmail);
        auditLog.setAction(action);
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setCreatedAt(java.time.LocalDateTime.now());
        auditRepo.save(auditLog);

}

}
