package com.project.taskmanager.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.project.taskmanager.service.CustomUserDetailsService;
import com.project.taskmanager.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter{

    @Autowired
    JwtService jwtService;

    @Autowired
    CustomUserDetailsService userservice;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header=request.getHeader("Authorization");
        if(header==null|| !header.startsWith("Bearer")){
            filterChain.doFilter(request, response);
            return ;
        }
        String token=header.substring(7);
        String username=jwtService.extractUsername(token);
        if(username!=null&&SecurityContextHolder.getContext().getAuthentication()==null){
            UserDetails userdetails=userservice.loadUserByUsername(username);
            if(jwtService.validateToken(token, userdetails)){
                UsernamePasswordAuthenticationToken authToken=new UsernamePasswordAuthenticationToken(userdetails,null,userdetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
    
}
