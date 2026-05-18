package com.project.taskmanager.service;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.project.taskmanager.model.RefreshToken;
import com.project.taskmanager.repository.RefreshTokenRepo;
import com.project.taskmanager.repository.UserRepo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    Logger log=LogManager.getLogger(JwtService.class);

    @Autowired
    private UserRepo userRepo;
    
    @Autowired
    private RefreshTokenRepo refreshTokenRepo;

    private final String SECRET_KEY="mysecretkeymysecretkeymysecretkeymysecretkeymysecretkeymysecretkey";
    public String generateToken(String username){
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+1000*60*60))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY)))
                .compact();
    }
    public boolean validateToken(String token,UserDetails userdetails){
        String username=extractUsername(token);
        return username.equals(userdetails.getUsername())&&!isExpired(token);
    }
    public boolean isExpired(String token) {
       return extractClaims(token).getExpiration().before(new Date());
    }
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }
    private Claims extractClaims(String token) {
       return Jwts.parser()
        .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY)))
        .build()
        .parseSignedClaims(token)
        .getPayload();
    }

    public String createRefreshToken(String email){
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(86400000*7)); //
        refreshToken.setUser(userRepo.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found for generating refresh token")));
        refreshTokenRepo.save(refreshToken);
        log.info("Refresh token created for email: {}", email);
        return refreshToken.getToken();
    }

    public RefreshToken findRefreshToken(String token){
        return refreshTokenRepo.findByToken(token).orElseThrow(()-> new RuntimeException("Invalid refresh token"));
    }

    public boolean verifyRefreshToken(RefreshToken refreshToken){
        if(refreshToken.getExpiryDate().isBefore(Instant.now())){
            log.info("Refresh token expired for email: {}", refreshToken.getUser().getEmail());
            refreshTokenRepo.delete(refreshToken);
            return false;
        }
        log.error("Refresh token verified successfully for email: {}", refreshToken.getUser().getEmail());
        return true;
    }
}
