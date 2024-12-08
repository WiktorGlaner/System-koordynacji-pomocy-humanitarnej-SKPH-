package org.ioad.spring.task.controller;


import org.ioad.spring.task.exceptions.ResourceNotFoundException;
import org.ioad.spring.task.model.Task;
import org.ioad.spring.task.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/createTask")
    public ResponseEntity<Task> createTask(@RequestBody Task task, @RequestParam("requestID") Long requestID,
                                           @RequestParam("resourcesIDs") List<Long> resourcesIDs) {
        Task createdTask = taskService.createTask(task, requestID, resourcesIDs);
        return ResponseEntity.ok(createdTask);
    }


    @PreAuthorize("hasRole('USER')")
    @GetMapping("/getTask")
    public ResponseEntity<Task> getTask(@RequestParam("id") long id) {
        Task task = taskService.getTask(id);
        return ResponseEntity.ok(task);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/getAllTasks")
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/editTask")
    public ResponseEntity<Task> editTask(@RequestParam("id") long id, @RequestBody Task updatedTask) {
        Task editedTask = taskService.editTask(id, updatedTask);
        return ResponseEntity.ok(editedTask);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/gradeTask")
    public ResponseEntity<Task> gradeTask(@RequestParam("id") long id, @RequestParam int grade) {
        Task gradedTask = taskService.gradeTask(id, grade); // GlobalExceptionHandler obsłuży wyjątek, jeśli wystąpi
        return ResponseEntity.ok(gradedTask);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/filterTasks")
    public ResponseEntity<List<Task>> filterTasks(
            @RequestParam(name = "location", required = false) String location,
            @RequestParam(name = "grade", required = false) Integer grade,
            @RequestParam(name = "organization", required = false) String organization,
            @RequestParam(name = "priority", required = false) Integer priority,
            @RequestParam(name = "status", required = false) String status) {

        List<Task> filteredTasks = taskService.filterTasks(location, grade, organization, priority, status);
        return ResponseEntity.ok(filteredTasks);
    }

//    @GetMapping
//    public List<Task> seeTasks() { return taskService.seeTasks(); }
}
