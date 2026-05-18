package com.project.taskmanager.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.project.taskmanager.model.OtpEntity;
import com.project.taskmanager.model.UserEntity;
import com.project.taskmanager.repository.OtpRepo;
import com.project.taskmanager.repository.UserRepo;


@Service
public class OtpService {

    Logger log = LogManager.getLogger(OtpService.class);

    @Autowired
    OtpRepo otpRepo;

    @Autowired
    JavaMailSender mailSender;

    @Autowired
    UserRepo userRepo;
    
    public String generateOtp(String email){
        String num=String.valueOf(100000+new SecureRandom().nextInt(900000));
        OtpEntity otp=new OtpEntity();
        otp.setEmail(email);
        otp.setOtp(num);
        otp.setVerified(false);
        otp.setCreatedAt(LocalDateTime.now());
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        otpRepo.save(otp);
        sendOtp(email,num);
        return "Otp sent to email , please verify";
    }
    public String verifyOtp(String email,String otp){
        OtpEntity otpentity=otpRepo.findTopByEmailOrderByCreatedAtDesc(email)
                                    .orElseThrow(() -> new RuntimeException("No Otp found for entered email address"));
        if(otpentity.getOtp().equals(otp)&&LocalDateTime.now().isBefore(otpentity.getExpiresAt())){
            UserEntity user=userRepo.findByEmail(email)
                                    .orElseThrow(()-> new RuntimeException("Email Not Found"));
            user.setVerified(true);
            userRepo.save(user);
            otpRepo.delete(otpentity);
            log.info("Otp verification successful for email: {}", email);
            return "Otp verification success";
        }
        else{
            log.error("Invalid Otp attempt for email: {}", email);
            throw new RuntimeException("Invalid Otp or Otp expired");
        }
    }
    public void sendOtp(String toEmail,String otp){
        SimpleMailMessage mail=new SimpleMailMessage();
        mail.setFrom("parallasrinivas@gmail.com");
        mail.setTo(toEmail);
        mail.setSubject("Otp verification");
        mail.setText("Your OTP to verify your taskmanager app is "+otp);
        mailSender.send(mail);
        log.info("Otp generated and sent to email: {}", toEmail);
    }
}
