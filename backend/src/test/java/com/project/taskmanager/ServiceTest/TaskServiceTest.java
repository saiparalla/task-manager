package com.project.taskmanager.ServiceTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.project.taskmanager.model.Task;
import com.project.taskmanager.model.UserEntity;
import com.project.taskmanager.repository.TaskRepo;
import com.project.taskmanager.repository.UserRepo;
import com.project.taskmanager.service.AuditService;
import com.project.taskmanager.service.TaskService;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {


    @InjectMocks
    TaskService service;
    @Mock
    TaskRepo repo;
    
    @Mock
    UserRepo userRepo;

    @Mock
    AuditService auditService;

    Task task;
    UserEntity user;

    @BeforeEach
    void init(){
        user = new UserEntity();
        user.setEmail("example@gmail.com");
        task=new Task();
        task.setId(1L);
        task.setTitle("learn");
        task.setUser(user);
    }

    @Test
    public void shouldAddTaskSuccessfully(){
        Authentication auth =mock(Authentication.class);
        SecurityContext sec=mock(SecurityContext.class);

        when(sec.getAuthentication())
                            .thenReturn(auth);
        when(auth.getName())
                            .thenReturn("example@gmail.com");
        when(userRepo.findByEmail("example@gmail.com"))
                            .thenReturn(Optional.of(user));
        when(repo.save(any(Task.class)))
                            .thenReturn(task);

        try(MockedStatic<SecurityContextHolder> secContext= mockStatic(SecurityContextHolder.class)){
        secContext.when(SecurityContextHolder::getContext).thenReturn(sec);
        Task restask=service.addTask(task);

        assertNotNull(restask);
        assertEquals(1L, restask.getId());
        assertEquals(user, restask.getUser());

        verify(repo).save(task);
        verify(auditService).log(user.getEmail(),"ADD","TASK",task.getId());
        }
    }
    
    @Test
    public void shouldThrowExceptionWhenSaveFails(){
        SecurityContext sec=mock(SecurityContext.class);
        Authentication auth=mock(Authentication.class);
        when(sec.getAuthentication()).thenReturn(auth);
        when(auth.getName()).thenReturn(user.getEmail());
        when(userRepo.findByEmail("example@gmail.com")).thenReturn(Optional.of(user));
        when(repo.save(any(Task.class))).thenReturn(null);

        try(MockedStatic<SecurityContextHolder> secContext=mockStatic(SecurityContextHolder.class)){
            secContext.when(SecurityContextHolder::getContext).thenReturn(sec);

            RuntimeException excep = assertThrows(RuntimeException.class, () -> service.addTask(task));

            assertEquals("Error while saving task", excep.getMessage());

            verify(auditService,never()).log(any(),any(),any(),any());
        }
    }

    @Test
    public void shouldUpdateTaskSuccessfully(){
        SecurityContext sec=mock(SecurityContext.class);
        Authentication auth=mock(Authentication.class);

        Task oldTask=new Task(); 
        oldTask.setId(1L);
        oldTask.setUser(user); 
        oldTask.setTitle("Old Title");
        oldTask.setDescription("Old Description");

        Task newTask=new Task();
        newTask.setTitle("New Title");
        newTask.setDescription("New Description");
        newTask.setStatus(true);

        when(sec.getAuthentication()).thenReturn(auth);
        when(auth.getName()).thenReturn("example@gmail.com");
        when(userRepo.findByEmail("example@gmail.com")).thenReturn(Optional.of(user));
        when(repo.findByIdAndUser(1L, user)).thenReturn(Optional.of(oldTask));
        when(repo.save(any(Task.class))).thenReturn(oldTask);

        try(MockedStatic<SecurityContextHolder> secContext=mockStatic(SecurityContextHolder.class)){
            secContext.when(SecurityContextHolder::getContext).thenReturn(sec);

            Task result=service.updateTask(newTask, 1);
            assertNotNull(result);
            assertEquals("New Title",result.getTitle());
            assertEquals("New Description", result.getDescription());
            assertEquals(true, result.isStatus());
            verify(repo).save(oldTask);
            verify(auditService).log(user.getEmail(), "UPDATE", "TASK",oldTask.getId());

        }
    }

    @Test
    public void shouldThrowExceptionWhenUpdateFails(){
        SecurityContext sec=mock(SecurityContext.class);
        Authentication auth=mock(Authentication.class);

        when(sec.getAuthentication()).thenReturn(auth);
        when(auth.getName()).thenReturn("example@gmail.com");
        when(userRepo.findByEmail("example@gmail.com")).thenReturn(Optional.of(user));
        when(repo.findByIdAndUser(1L,user)).thenReturn(Optional.empty());
        try(MockedStatic<SecurityContextHolder> mockSec=mockStatic(SecurityContextHolder.class)){
            mockSec.when(SecurityContextHolder::getContext).thenReturn(sec);

            RuntimeException ex= assertThrows(RuntimeException.class,()-> service.updateTask(task,1L));
            assertEquals("Task not found", ex.getMessage());

            verify(repo,never()).save(any(Task.class));
            verify(auditService,never()).log(any(),any(),any(),any());
        }
    }

    @Test
    public void shouldDeleteTaskSuccessfully(){
        SecurityContext sec=mock(SecurityContext.class);
        Authentication auth=mock(Authentication.class);

        when(sec.getAuthentication()).thenReturn(auth);
        when(userRepo.findByEmail("example@gmail.com")).thenReturn(Optional.of(user));
        when(auth.getName()).thenReturn("example@gmail.com");
        when(repo.findByIdAndUser(1L, user)).thenReturn(Optional.of(task));

        try(MockedStatic<SecurityContextHolder> secCon=mockStatic(SecurityContextHolder.class)){
            secCon.when(SecurityContextHolder::getContext).thenReturn(sec);

            service.deleteTask(1L);

            verify(repo).deleteByIdAndUser(1L, user);
            verify(auditService).log(user.getEmail(), "DELETE", "TASK", 1L);

        }
    }

    @Test
    public void shouldThrowExceptionWhenDeleteFails(){
        SecurityContext sec=mock(SecurityContext.class);
        Authentication auth=mock(Authentication.class);

        when(sec.getAuthentication()).thenReturn(auth);
        when(auth.getName()).thenReturn("example@gmail.com");
        when(userRepo.findByEmail("example@gmail.com")).thenReturn(Optional.of(user));
        when(repo.findByIdAndUser(1L, user)).thenReturn(Optional.empty());

        try(MockedStatic<SecurityContextHolder> secon=mockStatic(SecurityContextHolder.class)){
            secon.when(SecurityContextHolder::getContext).thenReturn(sec);

            RuntimeException ex=assertThrows(RuntimeException.class,()->service.deleteTask(1L));
            assertEquals("Task not found", ex.getMessage());

            verify(repo,never()).deleteByIdAndUser(1L, user);
            verify(auditService,never()).log(any(),any(),any(),any());
        }
    }

    @Test
    public void shouldReturnTaskSuccessfully(){
        SecurityContext sec=mock(SecurityContext.class);
        Authentication auth=mock(Authentication.class);

        when(sec.getAuthentication()).thenReturn(auth);
        when(auth.getName()).thenReturn("example@gmail.com");
        when(userRepo.findByEmail("example@gmail.com")).thenReturn(Optional.of(user));
        when(repo.findByIdAndUser(1L,user)).thenReturn(Optional.of(task));
        try(MockedStatic<SecurityContextHolder> secon=mockStatic(SecurityContextHolder.class)){
            secon.when(SecurityContextHolder::getContext).thenReturn(sec);
            Task resTask=service.getTask(1L);
            assertNotNull(resTask);
            assertEquals(task.getId(), resTask.getId());
            assertEquals(task.getTitle(),resTask.getTitle());
            
            verify(repo).findByIdAndUser(1L, user);
            verify(auditService).log(user.getEmail(),"VIEW","TASK",resTask.getId());
        }
    }

    @Test
    public void ShouldThrowExceptionWhenGetTaskFails(){
        SecurityContext sec=mock(SecurityContext.class);
        Authentication auth=mock(Authentication.class);

        when(sec.getAuthentication()).thenReturn(auth);
        when(auth.getName()).thenReturn("example@gmail.com");
        when(userRepo.findByEmail("example@gmail.com")).thenReturn(Optional.of(user));
        when(repo.findByIdAndUser(1L, user)).thenReturn(Optional.empty());

        try(MockedStatic<SecurityContextHolder> mocksec=mockStatic(SecurityContextHolder.class)){
            mocksec.when(SecurityContextHolder::getContext).thenReturn(sec);

            RuntimeException ex=assertThrows(RuntimeException.class, ()-> service.getTask(1L));
            assertEquals("Task not found", ex.getMessage());

            verify(repo).findByIdAndUser(1L, user);
            verify(auditService,never()).log(any(),any(),any(),any());
        }
    }

    @Test
    public void shouldReturnAllTaskSuccessfully(){
        SecurityContext sec=mock(SecurityContext.class);
        Authentication auth=mock(Authentication.class);

        List<Task> tasksList=List.of(task);
        Page<Task> page=new PageImpl<>(tasksList);

        when(sec.getAuthentication()).thenReturn(auth);
        when(auth.getName()).thenReturn("example@gmail.com");
        when(userRepo.findByEmail("example@gmail.com")).thenReturn(Optional.of(user));
        when(repo.findAllByUser(eq(user),any(PageRequest.class))).thenReturn(page);

        try(MockedStatic<SecurityContextHolder> mocksec=mockStatic(SecurityContextHolder.class)){
            mocksec.when(SecurityContextHolder::getContext).thenReturn(sec);
            List<Task> resTasks=service.getAllTasks(0,10,"id",null);
            assertNotNull(resTasks);
            assertEquals(1, resTasks.size());
            verify(repo).findAllByUser(eq(user),any(PageRequest.class));
            verify(auditService).log(user.getEmail(), "VIEW_ALL", "TASK", null);
        }

    }

    @Test
    public void shouldSearchTasksSuccessfully(){
        SecurityContext sec=mock(SecurityContext.class);
        Authentication auth=mock(Authentication.class);

        List<Task> tasksList=List.of(task);
        Page<Task> page=new PageImpl<>(tasksList);

        when(sec.getAuthentication()).thenReturn(auth);
        when(auth.getName()).thenReturn("example@gmail.com");
        when(userRepo.findByEmail("example@gmail.com")).thenReturn(Optional.of(user));
        when(repo.findByUserAndTitleContaining(eq(user),eq("learn"),any(PageRequest.class))).thenReturn(page);

        try(MockedStatic<SecurityContextHolder> mocksec=mockStatic(SecurityContextHolder.class)){
            mocksec.when(SecurityContextHolder::getContext).thenReturn(sec);

            List<Task> list=service.getAllTasks(0, 5, "title", "learn");

            assertNotNull(list);
            assertEquals(1, list.size());
                    verify(repo)
                .findByUserAndTitleContaining(
                        eq(user),
                        eq("learn"),
                        any(PageRequest.class)
                );

        verify(auditService)
                .log(user.getEmail(),
                        "SEARCH",
                        "TASK",
                        null);
    }

        }

}