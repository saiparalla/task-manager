package com.project.taskmanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.taskmanager.dto.JwtResponseDto;
import com.project.taskmanager.dto.LoginRequestDto;
import com.project.taskmanager.mapping.Mapper;
import com.project.taskmanager.service.AuthService;
import com.project.taskmanager.service.OtpService;

@RestController
public class AuthController {
    
    @Autowired
    AuthService authService;

    @Autowired
    OtpService otpService;



    @Autowired
    Mapper mapper;

    @PostMapping("/register")
    public String registerController(@RequestBody LoginRequestDto user){
        return authService.register(mapper.LoginRequestDtoToUserEntity(user));
    }
    @PostMapping("/login")
    public JwtResponseDto loginController(@RequestBody LoginRequestDto req){
        return authService.login(req);
    }

    @PostMapping("/verify-otp")
    public String otpVerification(@RequestParam String email, @RequestParam String otp){
        return otpService.verifyOtp(email,otp);
    }
    @PostMapping("/forget-password")
    public String forgetController(@RequestParam String email){
        return authService.forgetPassword(email);
    }
    @PostMapping("/reset")
    public String resetController(@RequestParam String email,@RequestParam String otp,@RequestParam String pwd){
        return authService.resetPassword(email, otp, pwd);
    }
    @PostMapping("/refresh-token")
    public JwtResponseDto refreshController(@RequestBody RefreshRequestDto refreshRequest){
        return authService.verifyRefreshToken(refreshRequest.getToken());
    }
    
}
