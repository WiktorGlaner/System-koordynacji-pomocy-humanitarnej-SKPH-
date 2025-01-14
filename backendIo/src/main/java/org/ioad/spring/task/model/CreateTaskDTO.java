package org.ioad.spring.task.model;

import java.util.List;

public class CreateTaskDTO {
    private Task task;
    private Long requestID;
    private List<String> volunteers;
    private List<ResourcePair> resources;

    // Gettery i settery
    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Long getRequestID() {
        return requestID;
    }

    public void setRequestID(Long requestID) {
        this.requestID = requestID;
    }

    public List<String> getVolunteers() {
        return volunteers;
    }

    public void setVolunteers(List<String> volunteers) {
        this.volunteers = volunteers;
    }

    public List<ResourcePair> getResources() {
        return resources;
    }

    public void setResources(List<ResourcePair> resources) {
        this.resources = resources;
    }
}
