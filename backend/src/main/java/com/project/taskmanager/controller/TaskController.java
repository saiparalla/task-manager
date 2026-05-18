package com.project.taskmanager.controller;

import com.project.taskmanager.dto.TaskRequestDto;
import com.project.taskmanager.dto.TaskResponseDto;
import com.project.taskmanager.mapping.Mapper;
import com.project.taskmanager.model.Task;
import com.project.taskmanager.service.TaskService;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class TaskController {

    TaskService service;
    Mapper mp;
    public TaskController(TaskService service,Mapper mp) {
        this.service = service;
        this.mp=mp;
    }
    
    @PostMapping("/tasks")
    public ResponseEntity<TaskResponseDto> AddTask(@RequestBody TaskRequestDto task){
        return ResponseEntity.status(HttpStatus.CREATED)
                    .body(mp.TaskToTaskResponseDto(service.addTask(mp.TaskRequestDtoToTask(task))));
    }
    
    @PatchMapping("/tasks/{id}")
    public ResponseEntity<TaskResponseDto> UpdateTask(@PathVariable long id, @RequestBody TaskRequestDto task){
        return
         ResponseEntity.status(HttpStatus.OK)
                        .body(mp.TaskToTaskResponseDto(service.updateTask(mp.TaskRequestDtoToTask(task),id)));
    }
    
    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> DeleteTask(@PathVariable long id){
        service.deleteTask(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    
    @GetMapping("/tasks/{id}")
    public ResponseEntity<TaskResponseDto> getTask(@PathVariable long id) {
        return ResponseEntity.status(HttpStatus.OK).body(mp.TaskToTaskResponseDto(service.getTask(id)));
    }
    
    @GetMapping("/tasks")
    public ResponseEntity<List<TaskResponseDto>> getTasks(@RequestParam(required = false,defaultValue = "0") int offset,
                                          @RequestParam(required = false,defaultValue = "5") int pagesize,
                                          @RequestParam(required = false,defaultValue = "Id") String sortBy,
                                          @RequestParam(required = false,defaultValue = "") String search
                                        ){
        List<Task> taskList= service.getAllTasks(offset,pagesize,sortBy,search);
        List<TaskResponseDto> resList=new ArrayList<>();
        for(Task t: taskList){
            resList.add(mp.TaskToTaskResponseDto(t));
        }
        return ResponseEntity.status(HttpStatus.OK).body(resList);

    }
}