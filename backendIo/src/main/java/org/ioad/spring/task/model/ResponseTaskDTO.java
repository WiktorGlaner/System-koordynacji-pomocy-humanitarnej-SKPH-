package org.ioad.spring.task.model;

import org.ioad.spring.resource.models.Resource;

import java.util.List;

public class ResponseTaskDTO {
    private Task task;
    List<Resource> resources;

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

}
