package com.project.taskmanager.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class TaskRequestDto {
    @NotBlank(message = "Title is required")
    private String title;
    private String description;
    @Future(message = "Date should be of future date")
    private LocalDateTime dueDate;
}
