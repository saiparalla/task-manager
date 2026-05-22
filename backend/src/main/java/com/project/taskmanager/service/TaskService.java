package com.project.taskmanager.service;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.project.taskmanager.model.Task;
import com.project.taskmanager.model.UserEntity;
import com.project.taskmanager.repository.TaskRepo;
import com.project.taskmanager.repository.UserRepo;

import jakarta.transaction.Transactional;

@Service
public class TaskService {

    Logger log=LogManager.getLogger(TaskService.class);

    @Autowired
    TaskRepo repo;
    
    @Autowired
    UserRepo userRepo;

    @Autowired
    AuditService auditService;

    public Task addTask(Task task) {
        UserEntity user=findUser();
        task.setUser(user);
        if(repo.save(task)!=null) {
            log.info("Task added successfully for user: {}", user.getEmail());
            auditService.log(user.getEmail(), "ADD", "TASK", task.getId());
            return task;
        }
        else {
            log.error("Error while saving task for user: {}", user.getEmail());
            throw new RuntimeException("Error while saving task");
        }
    }
    
    public Task updateTask(Task task,long id) {
        UserEntity user=findUser();
        Optional<Task> extask=repo.findByIdAndUser(id,user);
        if(extask.isPresent()) {
            Task t=extask.get();
            if(task.getTitle()!=null)
                t.setTitle(task.getTitle());
            t.setStatus(task.isStatus());
            if(task.getDescription()!=null)
                t.setDescription(task.getDescription());
            if(task.getDueDate()!=null)
                t.setDueDate(task.getDueDate());
            repo.save(t);
            auditService.log(user.getEmail(), "UPDATE", "TASK", t.getId());
            log.info("Task updated successfully for user: {}", user.getEmail());
            return t;
        }
        else {
            log.error("Task not found for user: {}", user.getEmail());
            throw new RuntimeException("Task not found");
        }
    }
    
    @Transactional
    public void deleteTask(Long id) {
        UserEntity user=findUser();
        if(repo.findByIdAndUser(id,user).isPresent()) {
            repo.deleteByIdAndUser(id,user);
            auditService.log(user.getEmail(), "DELETE", "TASK", id);
            log.info("Task deleted successfully for user: {}", user.getEmail());
        }
        else
        { 
            log.error("Task not found for user: {}", user.getEmail());
            throw new RuntimeException("Task not found");
        }
    }
    
    public Task getTask(Long id) {
        UserEntity user=findUser();
        Optional<Task> task=repo.findByIdAndUser(id, user);
        if(task.isPresent()) {
            auditService.log(user.getEmail(), "VIEW", "TASK", id);
            log.info("Task retrieved successfully for user: {}", user.getEmail());
            return task.get();
        }
        else {
            log.error("Task not found for user: {}", user.getEmail());
            throw new RuntimeException("Task not found");
        }
    }

    public List<Task> getAllTasks(int offset,int pagesize,String sortby,String search) {
        UserEntity user=findUser();
        if(search == null || search.isBlank()){
            auditService.log(user.getEmail(), "VIEW_ALL", "TASK", null);
            log.info("All tasks retrieved successfully for user: {}", user.getEmail());
            return repo.findAllByUser(user,PageRequest.of(offset,pagesize,Sort.by(sortby))).getContent();
        }
        else{
            auditService.log(user.getEmail(), "SEARCH", "TASK", null);
            log.info("Tasks searched with keyword '{}' for user: {}", search, user.getEmail());
            return repo.findByUserAndTitleContaining(user,search,PageRequest.of(offset,pagesize,Sort.by(sortby))).getContent();
        }
    }

    public UserEntity findUser(){
        Authentication auth=SecurityContextHolder.getContext().getAuthentication();
        String email=auth.getName();
        System.out.println(email);
        UserEntity user=userRepo.findByEmail(email).orElseThrow(()-> new RuntimeException("Can't get current user details"));
        return user;
    }
}
