package com.project.taskmanager.validation;

import java.util.HashMap;
import java.util.Map;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class FieldValidation {
    @ExceptionHandler()
    public Map<String,String> validationExceptionHandler(MethodArgumentNotValidException e){
        Map<String,String> mp=new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName= ((FieldError) error).getField();
            String message=error.getDefaultMessage();
            mp.put(fieldName,message);
    });
        return mp;
    }
}
