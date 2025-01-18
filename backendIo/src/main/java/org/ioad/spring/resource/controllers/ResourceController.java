package org.ioad.spring.resource.controllers;

import org.ioad.spring.resource.models.*;
import org.ioad.spring.resource.services.ResourceService;
import org.ioad.spring.resource.models.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasRole('ROLE_AUTHORITY') || hasRole('ROLE_ORGANIZATION') || hasRole('ROLE_VICTIM')")
    @GetMapping(path = "/resource")
    public ResponseEntity<List<Resource>> getResources(
            @RequestParam(required = false, name = "type") List<String> ResourceTypeValues,
            @RequestParam(required = false) Double organisationId,
            @RequestParam(required = false, name = "status") List<String> ResourceStatusValues) {
        List<Resource> resources = resourceService.getFilteredResources(ResourceTypeValues, organisationId, ResourceStatusValues);

        return ResponseEntity.ok(resources);
    }

    @GetMapping(path = "/resourceTypes")
    public ResponseEntity<List<String>> getResourceTypes() {
        if (resourceService.getResourceTypes().isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(resourceService.getResourceTypes());
    }

    @PreAuthorize("hasRole('ROLE_ORGANIZATION')" )
    @PostMapping(path = "/resource")
    public ResponseEntity<Resource> addResource(@RequestBody Resource resource) {
        return ResponseEntity.ok(resourceService.addResource(resource));
    }

    @PreAuthorize("hasRole('ROLE_ORGANIZATION')")
    @DeleteMapping(path = "/resource/{resourceId}")
    public ResponseEntity<String> removeResource(@PathVariable("resourceId") Long resourceId) {
        resourceService.removeResource(resourceId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ROLE_ORGANIZATION')")
    @PatchMapping(path = "/resource/{resourceId}")
    public ResponseEntity<String> modifyResource(@PathVariable("resourceId") Long resourceId,
                               @RequestBody ResourceUpdateDTO  request) {
        Location location = null;
        if (request.getLatitude() != null && request.getLongitude() != null) {
            location = new Location(request.getLatitude(), request.getLongitude());
        }
        resourceService.modifyResource(
                resourceId,
                request.getDescription(),
                location,
                request.getQuantity(),
                request.getStatus()
        );
        return ResponseEntity.ok("Resource modified successfully.");
    }

    @PreAuthorize("hasRole('ROLE_DONOR')")
    @PostMapping(path = "/donation")
    public ResponseEntity<Donation> addDonation(@RequestBody Donation donation) {
        return ResponseEntity.ok(resourceService.addDonation(donation));
    }

    @PreAuthorize("hasRole('ROLE_AUTHORITY') || hasRole('ROLE_DONOR')")
    @GetMapping(path = "/donation")
    public ResponseEntity<List<Donation>> getDonationByType(@RequestParam(required = false) String type) {
        List<Donation> donations;
        if (type != null) {
            donations = resourceService.getByDonationType(type);
        } else {
            donations = resourceService.getAllDonations();
        }

        return ResponseEntity.ok(donations);
    }

    @PreAuthorize("hasRole('ROLE_DONOR')")
    @GetMapping(path = "/donation/{donorId}")
    public ResponseEntity<List<Donation>> getByDonationDonorId(@PathVariable Long donorId) {
        List<Donation> donations = resourceService.getByDonationDonorId(donorId);

        return ResponseEntity.ok(donations);
    }

    @PreAuthorize("hasRole('ROLE_ORGANIZATION')")
    @PostMapping(path = "/assignresource/{resourceId}")
    public ResponseEntity<String> assignResource(@PathVariable("resourceId") Long resourceId,
                               @RequestParam(required = false) Long requestId,
                               @RequestParam(required = false) Double quantity) {
        resourceService.assignResource(resourceId, requestId, quantity);
        return ResponseEntity.ok("Resource assigned succesfully.");
    }

    @PreAuthorize("hasRole('ROLE_ORGANIZATION')")
    @DeleteMapping(path = "/revokeassignment/{assignmentId}")
    public ResponseEntity<String> revokeAssignment(@PathVariable("assignmentId") Long assignmentId) {
        resourceService.revokeAssignment(assignmentId);
        return ResponseEntity.ok("Resource revoked succesfully.");
    }

    @PreAuthorize("hasRole('ROLE_ORGANIZATION') || hasRole('ROLE_AUTHORITY')")
    @GetMapping(path = "/assignments/{resourceId}")
    public ResponseEntity<List<ResourceAssignment>> getResourceAssignments(@PathVariable("resourceId") Long resourceId) {
        List<ResourceAssignment> assignments = resourceService.getResourceAssignments(resourceId);

        return ResponseEntity.ok(assignments);
    }

    @PreAuthorize("hasRole('ROLE_ORGANIZATION') || hasRole('ROLE_AUTHORITY')")
    @GetMapping(path = "/resource/{resourceId}")
    public ResponseEntity<Resource> getResourceById(@PathVariable("resourceId") Long resourceId) {
        Resource resource = resourceService.getResourceById(resourceId);
        return ResponseEntity.ok(resource);
    }

    @PreAuthorize("hasRole('ROLE_ORGANIZATION') || hasRole('ROLE_AUTHORITY') || hasRole('ROLE_DONOR')")
    @GetMapping(path = "/assignments")
    public ResponseEntity<List<ResourceAssignment>> getAssignments() {
        List<ResourceAssignment> assignments = resourceService.getAssignments();

        return ResponseEntity.ok(assignments);
    }
}
