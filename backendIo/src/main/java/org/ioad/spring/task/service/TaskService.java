package org.ioad.spring.task.service;

import jakarta.validation.Valid;
import org.ioad.spring.request.models.EStatus;
import org.ioad.spring.request.models.Request;
import org.ioad.spring.request.services.RequestService;
import org.ioad.spring.resource.models.Resource;
import org.ioad.spring.resource.models.ResourceAssignment;
import org.ioad.spring.resource.services.ResourceService;
import org.ioad.spring.task.TaskServiceCommunication;
import org.ioad.spring.task.exceptions.*;
import org.ioad.spring.task.model.*;
import org.ioad.spring.task.repository.TaskRepo;
import org.ioad.spring.user.models.UserInfo;
import org.ioad.spring.user.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService implements TaskServiceCommunication {

    @Autowired
    TaskRepo taskRepo;

    public ResponseTaskDTO createTask(CreateTaskDTO createTaskDTO) {
        Task task = createTaskDTO.getTask();

        Long requestID = createTaskDTO.getRequestID();
        List<ResourcePair> resources = createTaskDTO.getResources();
        List<String> volunteerUsernames = createTaskDTO.getVolunteers();

        Request request = getRequest(requestID);
        if (request == null) {
            throw new RequestNotFoundException("Request with ID " + requestID + " not found.");
        }

        Optional<Task> existingTask = taskRepo.findByRequest(request);
        if (existingTask.isPresent()) {
            throw new DuplicateTaskException("A task for request ID " + requestID + " already exists.");
        }

        for (ResourcePair resourcePair : resources) {
            Resource resource = getResource(resourcePair.getId());
            if (resource == null) {
                throw new ResourceNotFoundException("Resource with ID " + resourcePair.getId() + " not found.");
            }
        }

        for (ResourcePair resourcePair : resources) {
            assignResource(resourcePair.getId(), requestID, resourcePair.getQuantity());
        }

        List<UserInfo> volunteers = new ArrayList<>();
        
        for (String volunteerUsername : volunteerUsernames) {
            Optional<UserInfo> volunteer = getUserByUsename(volunteerUsername);
            if (volunteer.isEmpty()) {
                throw new VolunteerNotFoundException("Volunteer with username " + volunteerUsername + " not found.");
            }
            volunteers.add(volunteer.get());
        }

        for (String volunteerUsername : volunteerUsernames) {
            changeActivity(true, volunteerUsername);
        }

        task.setVolunteers(volunteers);
        task.setRequest(request);
        changeRequestStatus(requestID, EStatus.IN_PROGRESS);

        try {
            taskRepo.save(task);
        } catch (Exception e) {
            throw new TaskSaveException("Failed to save the task");
        }

        return buildResponseTaskDTO(task);
    }

    public ResponseTaskDTO getTask(long id) {
        Task task = taskRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with ID " + id + " not found."));

        return buildResponseTaskDTO(task);
    }

    public ResponseTaskDTO endTask(@Valid long id) {
        return taskRepo.findById(id).map(existingTask -> {
            if (existingTask.getStatus() == TaskStatus.COMPLETED) {
                throw new IllegalStateException("Task is already completed and cannot be completed again.");
            }
            existingTask.setStatus(TaskStatus.COMPLETED);
            for (UserInfo volunteer : existingTask.getVolunteers()) {
                changeActivity(false, volunteer.getUser().getUsername());
            }
            changeRequestStatus(existingTask.getRequest().getRequestId(), EStatus.COMPLETED);
            taskRepo.save(existingTask);
            return buildResponseTaskDTO(existingTask);
        }).orElseThrow(() -> new TaskNotFoundException("Task not found with id " + id));
    }



    public List<ResponseTaskDTO> getAllTasks() {

        List<Task> taskList = taskRepo.findAll();
        List<ResponseTaskDTO> responseTaskDTOS = new ArrayList<>();

        for(Task task : taskList) {
            ResponseTaskDTO responseTaskDTO = buildResponseTaskDTO(task);
            responseTaskDTOS.add(responseTaskDTO);
        }
        return responseTaskDTOS;
    }

    public List<ResponseTaskDTO> getTasksByVolunteerUsername(String username) {

        List<Task> taskList = taskRepo.findByVolunteers_User_Username(username);
        List<ResponseTaskDTO> responseTaskDTOS = new ArrayList<>();

        for(Task task : taskList) {
            ResponseTaskDTO responseTaskDTO = buildResponseTaskDTO(task);
            responseTaskDTOS.add(responseTaskDTO);
        }

        return responseTaskDTOS;
    }

    public ResponseTaskDTO editTask(Long id, Task updatedTask) {
        return taskRepo.findById(id).map(existingTask -> {

            if (existingTask.getStatus() == TaskStatus.COMPLETED) {
                throw new IllegalStateException("Task with status 'COMPLETED' cannot be edited.");
            }

            validateTaskFields(updatedTask);

            existingTask.setTitle(updatedTask.getTitle());
            existingTask.setDescription(updatedTask.getDescription());
            existingTask.setLocation(updatedTask.getLocation());
            existingTask.setPriority(updatedTask.getPriority());

            taskRepo.save(existingTask);

            return buildResponseTaskDTO(existingTask);
        }).orElseThrow(() -> new TaskNotFoundException("Task not found with id " + id));
    }


    public ResponseTaskDTO gradeTask(Long id, int grade) {
        if (grade < 1 || grade > 5) {
            throw new IllegalArgumentException("Grade must be between 1 and 5.");
        }

        Task task = taskRepo.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id " + id));
    
        if (task.getStatus() == TaskStatus.IN_PROGRESS) {
            throw new IllegalStateException("Task must be completed before grading.");
        }

        if (task.getStatus() == TaskStatus.GRADED) {
            throw new IllegalStateException("Task is already graded and cannot be graded again.");
        }
    
        task.setGrade(grade);
        task.setStatus(TaskStatus.GRADED);
    
        task = taskRepo.save(task);
    
        return buildResponseTaskDTO(task);
    }
    

    
    public Double calculateAverageGradeForUser(String username) {
        List<Task> completedTasks = taskRepo.findByVolunteers_User_UsernameAndStatus(username, TaskStatus.COMPLETED);

        if (completedTasks.isEmpty()) {
            return 0.0;
        }

        int totalGrade = 0;
        for (Task task : completedTasks) {
            totalGrade += task.getGrade();
        }

        return (double) totalGrade / completedTasks.size();
    }



    public List<ResponseTaskDTO> filterTasks(String location, Integer grade, String organization, Integer priority, String status) {
        Specification<Task> spec = Specification.where(TaskSpecificationService.hasLocation(location))
                .and(TaskSpecificationService.hasGrade(grade))
                .and(TaskSpecificationService.hasOrganization(organization))
                .and(TaskSpecificationService.hasPriority(priority))
                .and(TaskSpecificationService.hasStatus(status));


        List<Task> taskList = taskRepo.findAll(spec);
        List<ResponseTaskDTO> responseTaskDTOS = new ArrayList<>();

        for(Task task : taskList) {
            ResponseTaskDTO responseTaskDTO = buildResponseTaskDTO(task);
            responseTaskDTOS.add(responseTaskDTO);
        }

        return responseTaskDTOS;
    }

    //Auxiliary methods

    private ResponseTaskDTO buildResponseTaskDTO(Task task) {
        ResponseTaskDTO responseTaskDTO = new ResponseTaskDTO();
        responseTaskDTO.setTask(task);

        List<ResourceAssignment> resourceAssignments = getResourcesInTask(task.getRequest().getRequestId());
        List<Resource> resources = new ArrayList<>();

        for (ResourceAssignment resourceAssignment : resourceAssignments) {
            resources.add(resourceAssignment.getResource());
        }

        responseTaskDTO.setResources(resources);
        return responseTaskDTO;
    }

    private void validateTaskFields(Task updatedTask) {
        if (updatedTask.getTitle() == null || updatedTask.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty.");
        }

        if (updatedTask.getLocation() == null || updatedTask.getLocation().trim().isEmpty()) {
            throw new IllegalArgumentException("Location cannot be empty.");
        }

        if (updatedTask.getPriority() == null || !isValidPriority(updatedTask.getPriority())) {
            throw new IllegalArgumentException("Invalid priority value. Must be one of: LOW, MEDIUM, HIGH, CRITICAL.");
        }

        if (updatedTask.getDescription() == null || updatedTask.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty.");
        }

        if (updatedTask.getDescription().length() > 500) {
            throw new IllegalArgumentException("Description cannot exceed 500 characters.");
        }
    }

    private boolean isValidPriority(TaskPriority priority) {
        try {
            TaskPriority.valueOf(priority.name());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }



    @Autowired
    RequestService requestService;

    @Autowired
    ResourceService resourceService;

    @Autowired
    UserService userService;

    @Override
    public List<Integer> getAllVolunteers() {
        return List.of();
    }

    @Override
    public List<Request> getRequestList() {
        RequestService requestService = new RequestService();
        return requestService.getAllRequests();
    }

    @Override
    public Request getRequest(long id) {
        return requestService.getRequestById(id);
    }

    @Override
    public Request changeRequestStatus(Long requestId, EStatus newStatus) {
        return requestService.changeRequestStatus(requestId, newStatus);
    }

    @Override
    public Resource getResource(long id) {
        return resourceService.getResourceById(id);
    }

    @Override
    public List<Resource> getResourceList() {
        return resourceService.getAvailableResources();
    }

    @Override
    public void assignResource(Long resourceId, Long requestId, Double quantity) {
        resourceService.assignResource(resourceId, requestId, quantity);
    }

    @Override
    public List<ResourceAssignment> getResourcesInTask(Long id) {
        return resourceService.getAssignmentsByRequestId(id);
    }

    @Override
    public Optional<UserInfo> getUserByUsename(String username) {
        try {
            return userService.getUser(username);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public void changeActivity(Boolean active, String username) {
        userService.changeActivity(active, username);
    }

}
