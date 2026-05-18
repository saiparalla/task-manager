package com.project.taskmanager.dto;

import lombok.Data;

@Data
public class JwtResponseDto {
    
    private String accessToken;
    private String refreshToken;

}
