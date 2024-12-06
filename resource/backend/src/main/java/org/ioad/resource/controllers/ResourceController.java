package org.ioad.resource.controllers;

import org.ioad.resource.models.ResourceStatus;
import org.ioad.resource.services.ResourceService;
import org.ioad.resource.models.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/resource")
public class ResourceController {

    private final ResourceService resourceService;

    @Autowired
    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping
    public List<Resource> getResource() {
        return resourceService.getResource();
    }

    @PostMapping
    public void addResource(@RequestBody Resource resource) {
        resourceService.addResource(resource);
    }

    @DeleteMapping(path = "{resourceId}")
    public void removeResource(@PathVariable("resourceId") Long resourceId) {
        resourceService.removeResource(resourceId);
    }

    @PutMapping(path = "{resourceId}")
    public void updateResource(@PathVariable("resourceId") Long resourceId,
                               @RequestParam(required = false) String location,
                               @RequestParam(required = false) Long organisationId,
                               @RequestParam(required = false) ResourceStatus status) {
        resourceService.updateResource(resourceId, location, organisationId, status);
    }
}
