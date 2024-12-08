package org.ioad.spring.resource.controllers;

import org.ioad.spring.resource.models.*;
import org.ioad.spring.resource.services.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class ResourceController {

    private final ResourceService resourceService;

    @Autowired
    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping(path = "/resource")
    public List<Resource> getResources(@RequestParam(required = false) String type) {
        if (type == null) {
            return resourceService.getAllResources();
        } else {
            ResourceType resourceType = ResourceType.valueOf(type.toUpperCase());
            return resourceService.getResourceByType(resourceType);
        }
    }

    @GetMapping(path = "/resource/available")
    public List<Resource> getAvailableResources() {
        return resourceService.getAvailableResources();
    }

    @PostMapping(path = "/resource")
    public void addResource(@RequestBody Resource resource) {
        resourceService.addResource(resource);
    }

    @DeleteMapping(path = "/resource/{resourceId}")
    public void removeResource(@PathVariable("resourceId") Long resourceId) {
        resourceService.removeResource(resourceId);
    }

    @PutMapping(path = "/resource/{resourceId}")
    public void modifyResource(@PathVariable("resourceId") Long resourceId,
                               @RequestParam(required = false) String location,
                               @RequestParam(required = false) Long organisationId,
                               @RequestParam(required = false) ResourceStatus status) {
        resourceService.modifyResource(resourceId, location, organisationId, status);
    }

    @PostMapping(path = "/donation")
    public void addDonation(@RequestBody Donation donation) {
        resourceService.addDonation(donation);
    }

    @GetMapping(path = "/donation")
    public List<Donation> getDonationByType(@RequestParam(required = false) String type) {
        ResourceType resourceType = ResourceType.valueOf(type.toUpperCase());
        return resourceService.getByDonationType(resourceType);
    }

    @GetMapping(path = "/donation/{donorId}")
    public List<Donation> getByDonationDonorId(@PathVariable Long donorId) {
        return resourceService.getByDonationDonorId(donorId);
    }

    @PostMapping(path = "/assignresource/{resourceId}")
    public void assignResource(@PathVariable("resourceId") Long resourceId,
                               @RequestParam(required = false) Long requestId,
                               @RequestParam(required = false) Double quantity) {
        resourceService.assignResource(resourceId, requestId, quantity);
    }

    @DeleteMapping(path = "/revokeassignment/{assignmentId}")
    public void revokeAssignment(@PathVariable("assignmentId") Long assignmentId) {
        resourceService.revokeAssignment(assignmentId);
    }

    @GetMapping(path = "/assignments/{resourceId}")
    public List<ResourceAssignment> getResourceAssignments(@PathVariable("resourceId") Long resourceId) {
        return resourceService.getResourceAssignments(resourceId);
    }
}
