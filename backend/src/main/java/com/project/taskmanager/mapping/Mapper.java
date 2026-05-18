package com.project.taskmanager.mapping;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.project.taskmanager.dto.LoginRequestDto;
import com.project.taskmanager.dto.TaskRequestDto;
import com.project.taskmanager.dto.TaskResponseDto;
import com.project.taskmanager.model.Role;
import com.project.taskmanager.model.Task;
import com.project.taskmanager.model.UserEntity;

@Component
public class Mapper {
    public Task TaskRequestDtoToTask(TaskRequestDto tRequestDto){
        Task task=new Task();
       // task.setId(id);
        task.setTitle(tRequestDto.getTitle());
        task.setDescription(tRequestDto.getDescription());
        task.setStatus(false);
        task.setCreatedAt(LocalDateTime.now());
        task.setDueDate(tRequestDto.getDueDate());
        return task;
    }

    public TaskResponseDto TaskToTaskResponseDto(Task task){
        TaskResponseDto tres=new TaskResponseDto();
        tres.setCreatedAt(task.getCreatedAt());
        tres.setDescription(task.getDescription());
        tres.setDueDate(task.getDueDate());
        tres.setId(task.getId());
        tres.setStatus(task.isStatus());
        tres.setTitle(task.getTitle());
        return tres;

    }

    public UserEntity LoginRequestDtoToUserEntity(LoginRequestDto lRequestDto){
        UserEntity user=new UserEntity();
        user.setEmail(lRequestDto.getEmail());
        user.setUserName(lRequestDto.getUsername());
        user.setPassword(lRequestDto.getPassword());
        user.setRole(Role.USER);
        return user;
    }
}
