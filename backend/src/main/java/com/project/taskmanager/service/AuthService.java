package com.project.taskmanager.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.project.taskmanager.dto.JwtResponseDto;
import com.project.taskmanager.dto.LoginRequestDto;
import com.project.taskmanager.model.OtpEntity;
import com.project.taskmanager.model.RefreshToken;
import com.project.taskmanager.model.UserEntity;
import com.project.taskmanager.repository.OtpRepo;
import com.project.taskmanager.repository.UserRepo;

@Service
public class AuthService {

    @Autowired
    OtpService otpService;

    @Autowired
    JwtService jwtService;
    
    @Autowired
    UserRepo userRepo;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    OtpRepo otpRepo;

    public String register(UserEntity user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if(userRepo.save(user)==null)
            return "User not registered";
        else
        {
            otpService.generateOtp(user.getEmail());
            return "Your otp is sent to email , please verify it now ";
        }
    }

    public JwtResponseDto login(@RequestBody LoginRequestDto req){
        Authentication authentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
        if(authentication.isAuthenticated()){
            UserEntity user=userRepo.findByEmail(req.getEmail()).orElseThrow(()-> new UsernameNotFoundException("Email Not Found"));
            if(!user.isVerified()){
                otpService.generateOtp(req.getEmail());
                throw new RuntimeException("Please verify your email first");
            }
            else{
                JwtResponseDto res=new JwtResponseDto();
                res.setRefreshToken(jwtService.createRefreshToken(req.getEmail()));
                res.setAccessToken(jwtService.generateToken(req.getEmail()));
                return res;
            }
        }
        else
            throw new UsernameNotFoundException("Invalid login details ");
    }

    public String forgetPassword(String email) {
        Optional<UserEntity> user=userRepo.findByEmail(email);
        if(user.isPresent())
        {
        otpService.generateOtp(email);
        return "OTP send for resetting password";
        }
        else{
            return "Email not found";
        }
    }

    public String resetPassword(String email,String otp,String pwd){
        OtpEntity otpentity=otpRepo.findTopByEmailOrderByCreatedAtDesc(email)
                                    .orElseThrow(() -> new RuntimeException("No Otp found for entered email address"));
        if(otpentity.getOtp().equals(otp)&&LocalDateTime.now().isBefore(otpentity.getExpiresAt())){
            UserEntity user=userRepo.findByEmail(email)
                                    .orElseThrow(()-> new RuntimeException("Email Not Found"));
        user.setPassword(passwordEncoder.encode(pwd));
        if(userRepo.save(user)!=null)
            return "Password Changed Successfully";
        else
            return "There was a problem while resetting password";
    }
        else
            return "There was a problem while resetting password";
    }

    public JwtResponseDto verifyRefreshToken(String token){
        RefreshToken refreshToken=jwtService.findRefreshToken(token);
        if(jwtService.verifyRefreshToken(refreshToken)){
            String newAccessToken=jwtService.generateToken(refreshToken.getUser().getEmail());
            JwtResponseDto response = new JwtResponseDto();
            response.setAccessToken(newAccessToken);
            response.setRefreshToken(token);
            return response;
        }
        else{
            throw new RuntimeException("Invalid refresh token");
        }
    }
}
