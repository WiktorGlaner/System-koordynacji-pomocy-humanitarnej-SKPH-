package org.ioad.spring.task.service;

import org.ioad.spring.request.models.Request;
import org.ioad.spring.request.services.RequestService;
import org.ioad.spring.resource.models.Resource;
import org.ioad.spring.resource.services.ResourceService;
import org.ioad.spring.task.TaskServiceCommunication;
import org.ioad.spring.task.exceptions.ResourceNotFoundException;
import org.ioad.spring.task.model.Task;
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

    public Task createTask(Task task, long requestID, List<Long> resourcesIDs) {
        Request request = getRequest(requestID);
        if (request == null) {
            throw new ResourceNotFoundException("Request with ID " + requestID + " not found.");
        }

        List<Resource> resourceList = new ArrayList<>();

        for (long resource : resourcesIDs) {
            resourceList.add(getResource(resource));
        }

        task.setResourceList(resourceList);
        task.setRequest(request);

        return taskRepo.save(task);
    }

    public Task getTask(long id) {
        return taskRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with ID " + id + " not found."));
    }


    public List<Task> getAllTasks() {
        return taskRepo.findAll();
    }

    public Task editTask(Long id, Task updatedTask) {

        return taskRepo.findById(id).map(existingTask -> {
            existingTask.setTitle(updatedTask.getTitle());
            existingTask.setDescription(updatedTask.getDescription());
            existingTask.setOrganization(updatedTask.getOrganization());
            existingTask.setLocation(updatedTask.getLocation());
            existingTask.setStatus(updatedTask.getStatus());
            existingTask.setPriority(updatedTask.getPriority());


            return taskRepo.save(existingTask);
        }).orElseThrow(() -> new ResourceNotFoundException("Task not found with id " + id));
    }

    public Task gradeTask(Long id, int grade) {

        return taskRepo.findById(id).map(existingTask -> {
            existingTask.setGrade(grade);

            return taskRepo.save(existingTask);
        }).orElseThrow(() -> new ResourceNotFoundException("Task not found with id " + id));
    }

    public List<Task> filterTasks(String location, Integer grade, String organization, Integer priority, String status) {
        Specification<Task> spec = Specification.where(TaskSpecificationService.hasLocation(location))
                .and(TaskSpecificationService.hasGrade(grade))
                .and(TaskSpecificationService.hasOrganization(organization))
                .and(TaskSpecificationService.hasPriority(priority))
                .and(TaskSpecificationService.hasStatus(status));

        return taskRepo.findAll(spec);
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

    //public List<Task> seeTasks();
}
