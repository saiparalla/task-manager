package com.project.taskmanager.dto;

import lombok.Data;

@Data
public class RegisterRequestDto {
    private String userName;
    private String password;
    private String role;
}
