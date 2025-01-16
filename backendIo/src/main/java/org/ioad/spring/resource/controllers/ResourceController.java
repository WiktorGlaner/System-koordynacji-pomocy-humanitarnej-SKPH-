package org.ioad.spring.resource.controllers;

import org.apache.coyote.Response;
import org.ioad.spring.resource.models.*;
import org.ioad.spring.resource.services.ResourceService;
import org.ioad.spring.resource.models.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@CrossOrigin(origins = "*")
public class ResourceController {

    private final ResourceService resourceService;

    @Autowired
    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping(path = "/resource")
    public ResponseEntity<List<Resource>> getResources(
            @RequestParam(required = false, name = "type") List<String> ResourceTypeValues,
            @RequestParam(required = false) Double organisationId,
            @RequestParam(required = false, name = "status") List<String> ResourceStatusValues) {
        List<Resource> resources = resourceService.getFilteredResources(ResourceTypeValues, organisationId, ResourceStatusValues);
        if (resources.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(resources);
    }

    @GetMapping(path = "/resourceTypes")
    public ResponseEntity<List<String>> getResourceTypes() {
        if (resourceService.getResourceTypes().isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(resourceService.getResourceTypes());
    }

//    @GetMapping(path = "/resource/available")
//    public List<Resource> getAvailableResources() {
//        return resourceService.getAvailableResources();
//    }

    @PostMapping(path = "/resource")
    public ResponseEntity<String> addResource(@RequestBody Resource resource) {
        resourceService.addResource(resource);
        return ResponseEntity.ok("Resource added succesfully.");
    }

    @DeleteMapping(path = "/resource/{resourceId}")
    public ResponseEntity<String> removeResource(@PathVariable("resourceId") Long resourceId) {
        resourceService.removeResource(resourceId);
        return ResponseEntity.ok("Resource removed succesfully.");
    }

    @PatchMapping(path = "/resource/{resourceId}")
    public ResponseEntity<String> modifyResource(@PathVariable("resourceId") Long resourceId,
                               @RequestParam(required = false) String description,
                               @RequestParam(required = false) Double latitude,
                               @RequestParam(required = false) Double longitude,
                               @RequestParam(required = false) Double quantity,
                               @RequestParam(required = false) ResourceStatus status) {
        Location location = null;
        if (latitude != null && longitude != null) {
            location = new Location(latitude, longitude);
        }
        resourceService.modifyResource(resourceId, description, location, quantity, status);
        return ResponseEntity.ok("Resource modified successfully");
    }

    @PostMapping(path = "/donation")
    public ResponseEntity<String> addDonation(@RequestBody Donation donation) {
        resourceService.addDonation(donation);
        return ResponseEntity.ok("Donation removed succesfully.");
    }

    @GetMapping(path = "/donation")
    public ResponseEntity<List<Donation>> getDonationByType(@RequestParam(required = false) String type) {
        ResourceType resourceType = ResourceType.valueOf(type.toUpperCase());
        List<Donation> donations = resourceService.getByDonationType(resourceType);

        if (donations.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(donations);
    }

    @GetMapping(path = "/donation/{donorId}")
    public ResponseEntity<List<Donation>> getByDonationDonorId(@PathVariable Long donorId) {
        List<Donation> donations = resourceService.getByDonationDonorId(donorId);

        if (donations.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(donations);
    }

    @PostMapping(path = "/assignresource/{resourceId}")
    public ResponseEntity<String> assignResource(@PathVariable("resourceId") Long resourceId,
                               @RequestParam(required = false) Long requestId,
                               @RequestParam(required = false) Double quantity) {
        resourceService.assignResource(resourceId, requestId, quantity);
        return ResponseEntity.ok("Resource assigned succesfully.");
    }

    @DeleteMapping(path = "/revokeassignment/{assignmentId}")
    public ResponseEntity<String> revokeAssignment(@PathVariable("assignmentId") Long assignmentId) {
        resourceService.revokeAssignment(assignmentId);
        return ResponseEntity.ok("Resource revoked succesfully.");
    }

    @GetMapping(path = "/assignments/{resourceId}")
    public ResponseEntity<List<ResourceAssignment>> getResourceAssignments(@PathVariable("resourceId") Long resourceId) {
        List<ResourceAssignment> assignments = resourceService.getResourceAssignments(resourceId);

        if (assignments.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(assignments);
    }

    @GetMapping(path = "/resource/{resourceId}")
    public ResponseEntity<Resource> getResourceById(@PathVariable("resourceId") Long resourceId) {
        Resource resource = resourceService.getResourceById(resourceId);
        return ResponseEntity.ok(resource);
    }

    @GetMapping(path = "/assignments")
    public ResponseEntity<List<ResourceAssignment>> getAssignmentsByRequestId(
            @RequestParam(required = false) Long requestId) {
        List<ResourceAssignment> assignments = resourceService.getAssignmentsByRequestId(requestId);

        if (assignments.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(assignments);
    }
}
