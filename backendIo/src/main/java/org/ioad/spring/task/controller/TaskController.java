package org.ioad.spring.task.controller;


import jakarta.validation.Valid;
import org.ioad.spring.task.exceptions.MissingRequiredDataException;
import org.ioad.spring.task.model.*;
import org.ioad.spring.task.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;


@RequestMapping("/tasks")
@RestController
@CrossOrigin(origins = "*")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PreAuthorize("hasRole('ROLE_ORGANIZATION')")
    @PostMapping("/createTask")
    public ResponseEntity<ResponseTaskDTO> createTask(@Valid @RequestBody CreateTaskDTO createTaskDTO) {
        validateCreateTaskData(createTaskDTO);
        ResponseTaskDTO createdTask = taskService.createTask(createTaskDTO);
        return ResponseEntity.ok(createdTask);
    }

    @GetMapping("/calculateGrade")
    public ResponseEntity<Double> calculateAverageGradeForUser(@RequestParam String username) {
        return ResponseEntity.ok(taskService.calculateAverageGradeForUser(username));
    }
    
    @PreAuthorize("hasRole('ROLE_ORGANIZATION')" + "or hasRole('ROLE_VOLUNTEER')")
    @GetMapping("/getTask")
    public ResponseEntity<ResponseTaskDTO> getTask(@Valid @RequestParam("id") long id) {
        ResponseTaskDTO task = taskService.getTask(id);
        return ResponseEntity.ok(task);
    }

    @PreAuthorize("hasRole('ROLE_ORGANIZATION')" + "or hasRole('ROLE_VOLUNTEER')")
    @PutMapping("/endTask")
    public ResponseEntity<ResponseTaskDTO> endTask(@Valid @RequestParam("id") long id) {
        ResponseTaskDTO task = taskService.endTask(id);
        return ResponseEntity.ok(task);
    }

    @PreAuthorize("hasRole('ROLE_ORGANIZATION')")
    @GetMapping("/getAllTasks")
    public ResponseEntity<List<ResponseTaskDTO>> getAllTasks() {
        List<ResponseTaskDTO> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    @PreAuthorize("hasRole('ROLE_VOLUNTEER')")
    @GetMapping("/volunteerTasks")
    public List<ResponseTaskDTO> getTasksByVolunteerUsername(@RequestParam String username) {

        return taskService.getTasksByVolunteerUsername(username);
    }

    @PreAuthorize("hasRole('ROLE_ORGANIZATION')")
    @PutMapping("/editTask")
    public ResponseEntity<ResponseTaskDTO> editTask(@RequestParam("id") long id, @RequestBody Task updatedTask) {
        ResponseTaskDTO editedTask = taskService.editTask(id, updatedTask);
        return ResponseEntity.ok(editedTask);
    }

    @PreAuthorize("hasRole('ROLE_ORGANIZATION')")    
    @PutMapping("/gradeTask")
    public ResponseEntity<ResponseTaskDTO> gradeTask(@RequestParam("id") long id, @RequestParam int grade) {
        ResponseTaskDTO gradedTask = taskService.gradeTask(id, grade); // GlobalExceptionHandler obsłuży wyjątek, jeśli wystąpi
        return ResponseEntity.ok(gradedTask);
    }

    @PreAuthorize("hasRole('ROLE_ORGANIZATION')" + "or hasRole('ROLE_VOLUNTEER')")
    @GetMapping("/filterTasks")
    public ResponseEntity<List<ResponseTaskDTO>> filterTasks(
            @RequestParam(name = "location", required = false) String location,
            @RequestParam(name = "grade", required = false) Integer grade,
            @RequestParam(name = "organization", required = false) String organization,
            @RequestParam(name = "priority", required = false) Integer priority,
            @RequestParam(name = "status", required = false) String status) {

        List<ResponseTaskDTO> filteredTasks = taskService.filterTasks(location, grade, organization, priority, status);
        return ResponseEntity.ok(filteredTasks);
    }

    //Auxiliary methods

    public void validateCreateTaskData(CreateTaskDTO createTaskDTO) {
        if (createTaskDTO == null) {
            throw new MissingRequiredDataException("Task data cannot be null.");
        }

        Task task = createTaskDTO.getTask();
        if (task == null) {
            throw new MissingRequiredDataException("Task must be provided.");
        }

        // Sprawdź, czy pole 'title' w Task jest niepuste
        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            throw new MissingRequiredDataException("Task title must not be empty.");
        }

        if (task.getLocation() == null || task.getLocation().trim().isEmpty()) {
            throw new MissingRequiredDataException("Task location must not be empty.");
        }

        if (task.getPriority() == null) {
            throw new MissingRequiredDataException("Priority must be provided.");
        }

        if (!isValidPriority(task.getPriority())) {
            throw new MissingRequiredDataException("Invalid priority value. Must be one of: LOW, MEDIUM, HIGH, CRITICAL.");
        }

        if (createTaskDTO.getRequestID() == null) {
            throw new MissingRequiredDataException("Request ID must be provided.");
        }

        List<String> volunteers = createTaskDTO.getVolunteers();

        if (volunteers == null || volunteers.isEmpty()) {
            throw new MissingRequiredDataException("At least one volunteer must be provided.");
        }

        if (volunteers.stream().anyMatch(volunteer -> volunteer == null || volunteer.trim().isEmpty())) {
            throw new MissingRequiredDataException("Volunteer names must not be null or empty.");
        }

        List<ResourcePair> resources = createTaskDTO.getResources();
        if (resources == null || resources.isEmpty()) {
            throw new MissingRequiredDataException("At least one resource must be provided.");
        }


//        if (createTaskDTO.getVolunteers() == null || createTaskDTO.getVolunteers().isEmpty()) {
//            throw new MissingRequiredDataException("At least one volunteer must be assigned to the task.");
//        }
    }

    private boolean isValidPriority(TaskPriority priority) {
        try {
            TaskPriority.valueOf(priority.name());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
