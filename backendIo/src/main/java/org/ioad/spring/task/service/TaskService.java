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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskService implements TaskServiceCommunication {

    @Autowired
    TaskRepo taskRepo;

    public ResponseTaskDTO createTask(CreateTaskDTO createTaskDTO) {
        Task task = createTaskDTO.getTask();

        Long requestID = createTaskDTO.getRequestID();
        List<ResourcePair> resources = createTaskDTO.getResources();
//        List<Long> volunteerIds = createTaskDTO.getVolunteers();

        Request request = getRequest(requestID);
        if (request == null) {
            throw new RequestNotFoundException("Request with ID " + requestID + " not found.");
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

//        for (Long volunteerId : volunteerIds) {
//            getVoluntirbyID
//            if(volunteer == null) {
//                throw new VolunteerNotFoundException("Volunteer with ID " + volunteer + " not found.");
//            }
//             jesli znajdziesz wolontiusza przypisz go to tymczasowej listy,
//             która uzjesz w seterze zeby ustawic wolontariuszy
//        }


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

        Task task = taskRepo.findById(id).map(existingTask -> {
            if (!existingTask.getStatus().equals(TaskStatus.COMPLETED)) {
                throw new IllegalStateException("Task must be completed before grading.");
            }

            existingTask.setGrade(grade);
            return taskRepo.save(existingTask);
        }).orElseThrow(() -> new TaskNotFoundException("Task not found with id " + id));

        return buildResponseTaskDTO(task);
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
}
