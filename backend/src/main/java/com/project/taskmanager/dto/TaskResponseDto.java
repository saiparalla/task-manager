package com.project.taskmanager.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TaskResponseDto {
    private Long id;
    private String title;
    private String description;
    private boolean status;
    private LocalDateTime createdAt;
    private LocalDateTime dueDate;
}
