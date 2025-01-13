package org.ioad.spring.resource.services;

import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import org.ioad.spring.resource.exceptions.*;
import org.ioad.spring.resource.models.*;
import org.ioad.spring.resource.repositories.ResourceAssignmentRepository;
import org.ioad.spring.resource.repositories.ResourceRepository;
import org.ioad.spring.resource.models.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ResourceService implements IResourceService {

    private final ResourceRepository resourceRepository;

    private final ResourceAssignmentRepository resourceAssignmentRepository;

    @Autowired
    public ResourceService(ResourceRepository resourceRepository, ResourceAssignmentRepository resourceAssignmentRepository) {
        this.resourceRepository = resourceRepository;
        this.resourceAssignmentRepository = resourceAssignmentRepository;
    }

    @Override
    public List<Resource> getAllResources() {
        return resourceRepository.findAll();
    }

    @Override
    public Resource getResourceById(long id) {
        return resourceRepository.findByResourceId(id);
    }

    @Override
    public List<ResourceAssignment> getAllResourceAssignment() {
        return resourceAssignmentRepository.findAll();
    }

    public void addResource(Resource resource) {
        //add validation later
        resourceRepository.save(resource);
    }

    public void addDonation(Donation donation) {
        //add validation later
        resourceRepository.save(donation);
    }

    public void removeResource(Long resourceId) {
        boolean exists = resourceRepository.existsById(resourceId);
        if (!exists) {
            throw new ResourceNotFound("Resource with id " + resourceId + " does not exists" );
        }
        resourceRepository.deleteById(resourceId);
    }

    @Override
    public List<Donation> getAllDonations() {
        return resourceRepository.findAllDonation();
    }

    @Transactional
    @Override
    public void modifyResource(Long resourceId, Location location, Long organisationId, ResourceStatus status) {
        Resource resource = resourceRepository.findById(resourceId).orElseThrow(
                () -> new ResourceNotFound("resource with " + resourceId + " does not exist"));

        if (location != null &&
                !Objects.equals(resource.getLocation(), location)) {
            System.out.println(resource.getLocation());
            System.out.println(location);
            resource.setLocation(location);
        }
        if (organisationId != null &&
                organisationId > 0 &&
                !Objects.equals(resource.getOrganisationId(), organisationId)) {
            resource.setOrganisationId(organisationId);
        }

        if (status != null &&
                !Objects.equals(resource.getStatus(), status)) {
            resource.setStatus(status);
        }
    }

    @Override
    public List<Resource> getResourceByType(ResourceType resourceType) {
        return resourceRepository.getByResourceType(resourceType);
    }

    @Override
    public Resource getResourceById(Long resourceId) {
        return resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFound("Resource not found with id: " + resourceId));
    }

    @Override
    public List<ResourceAssignment> getAssignmentsByRequestId(Long requestId) {
        return resourceAssignmentRepository.findByRequestId(requestId);
    }

    @Override
    public List<Donation> getByDonationType(ResourceType resourceType) {
        return resourceRepository.getByDonationType(resourceType);
    }

    @Override
    public List<Resource> getAvailableResources() {
        return resourceRepository.findByStatus(ResourceStatus.AVAILABLE);
    }

    @Override
    public List<ResourceAssignment> getResourceAssignments(Long resourceId) {
        return resourceAssignmentRepository.findByResourceId(resourceId);
    }

    public double getAvailableQuantity(Resource resource) {
        Double currentAssigned = resourceRepository.getTotalAssignedQuantity(resource.getId());

        return resource.getQuantity() - currentAssigned;
    }

    public void assignResource(Long resourceId, Long requestId, Double quantity) {
        Resource resource = resourceRepository.findById(resourceId).orElseThrow(
                () -> new ResourceNotFound("Resource with id " + resourceId + " not found"));

        if (resource.isExpired()) {
            throw new ResourceExpiredError("Resource with id " + resourceId + " has expired.");
        }

        if (resource.getStatus() != ResourceStatus.AVAILABLE) {
            throw new ResourceUnavailableException("Resource with id " + resourceId + " is unavailable.");
        }

        Double availableQuantity = this.getAvailableQuantity(resource);

        if (availableQuantity < quantity) {
            throw new InsufficientResourceException("Not enough quantity to assign resource with id "
                    + resourceId + ", Available: " + availableQuantity + ", Requested: " + quantity);
        } else if (availableQuantity.equals(quantity)) {
            resource.outOfComission();
        } else {
            resource.restoreResource();
        }

        ResourceAssignment assignment = new ResourceAssignment(resource, requestId, quantity);

        resourceAssignmentRepository.save(assignment);
    }

    public void revokeAssignment(Long assignmentId) {
        ResourceAssignment resourceAssignment = resourceAssignmentRepository.findById(assignmentId).orElseThrow(
                () -> new ResourceAssignmentNotFound("Assignment with id " + assignmentId +" not found"));

        resourceAssignment.getResource().restoreResource();
        resourceAssignmentRepository.deleteById(assignmentId);
    }

    @Override
    public List<Donation> getByDonationDonorId(Long donorId) {
        return resourceRepository.getByDonationDonorId(donorId);
    }

    public List<Resource> getFilteredResources(List<String> resourceTypeValues, Double organisationId, List<String> resourceStatusValues) {
        return resourceRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (resourceTypeValues != null && !resourceTypeValues.isEmpty()) {
                List<ResourceType> resourceTypes = resourceTypeValues.stream()
                        .map(value -> ResourceType.valueOf(value.toUpperCase()))
                        .toList();
                predicates.add(root.get("resourceType").in(resourceTypes));
            }
            if (organisationId != null) {
                predicates.add(criteriaBuilder.equal(root.get("organisationId"), organisationId));
            }
            if (resourceStatusValues != null && !resourceStatusValues.isEmpty()) {
                List<ResourceStatus> resourceStatuses = resourceStatusValues.stream()
                        .map(value -> ResourceStatus.valueOf(value.toUpperCase()))
                        .toList();
                predicates.add(root.get("status").in(resourceStatuses));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
}
