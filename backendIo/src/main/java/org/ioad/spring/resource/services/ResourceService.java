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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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


    void validateResource(Resource resource) {
        if (resource.getQuantity() == null || resource.getQuantity() <= 0) {
            throw new InvalidArgument("Cannot assign negative quantity of: " + resource.getQuantity());
        } else if (resource.getLocation() == null) {
            throw new InvalidArgument("Cannot assign with null location.");
        }else if(resource.getLocation().getLatitude() < -90 || resource.getLocation().getLatitude() > 90 ||
                resource.getLocation().getLongitude() < -180 || resource.getLocation().getLongitude() > 180) {
            throw new InvalidArgument("Cannot assign wrong value of longitude: " + resource.getLocation().getLongitude()
                    + " or latitude: " + resource.getLocation().getLatitude());
        }else if(resource.getName() == null || resource.getName().isEmpty()){
            throw new InvalidArgument("Cannot assign with empty name");
        }else if(requiresExpirationDate(resource.getResourceType()) && (resource.getExpDate() == null || LocalDate.now().isAfter(resource.getExpDate()))) {
            throw new InvalidArgument("The resource type '" + resource.getResourceType()
                    + "' requires a valid expiration date. Provided expiration date is missing. "
                    + "Please specify an expiration date is in the future.");
        }else if(resource.getUnit() == null) {
            throw new InvalidArgument("Cannot add resource with null unit.");
        }else if(resource.getOrganisationId() == null) {
            throw new InvalidArgument("Cannot add resource with null organisationId");
        }
    }

    public Resource addResource(Resource resource) {
        validateResource(resource);
        return resourceRepository.save(resource);
    }

    public void addDonation(Donation donation) {
        validateResource(donation);
        resourceRepository.save(donation);
    }

    public void removeResource(Long resourceId) {
        boolean exists = resourceRepository.existsById(resourceId);
        List<ResourceAssignment> resource = resourceAssignmentRepository.findByResourceId(resourceId);

        if (!resource.isEmpty()) {
            throw new InvalidArgument("Resource with id " + resourceId + " is assigned.");
        }

        if (!exists) {
            throw new ResourceNotFound("Resource with id " + resourceId + " does not exists." );
        }
        resourceRepository.deleteById(resourceId);
    }

    @Override
    public List<Donation> getAllDonations() {
        return resourceRepository.findAllDonation();
    }

    @Transactional
    @Override
    public void modifyResource(Long resourceId, String description, Location location, Double quantity, String status) {
        Resource resource = resourceRepository.findById(resourceId).orElseThrow(
                () -> new ResourceNotFound("resource with " + resourceId + " does not exist"));

        if (description != null &&
                !Objects.equals(resource.getDescription(), description)) {
            resource.setDescription(description);
        }

        if (location != null &&
                !Objects.equals(resource.getLocation(), location)) {
            resource.setLocation(location);
        }

        if (quantity != null &&
                !Objects.equals(resource.getQuantity(), quantity)) {
            resource.setQuantity(quantity);
        }

        ResourceStatus resourceStatus;
        try {
            resourceStatus = ResourceStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidArgument("No resource status with name: " + status);
        }

        if (!Objects.equals(resource.getStatus(), resourceStatus)) {
            resource.setStatus(resourceStatus);
        }
        validateResource(resource);
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
    public List<Donation> getByDonationType(String type) {
        ResourceType resourceType;
        try {
            resourceType = ResourceType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidArgument("No donation with type: " + type);
        }
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

    @Transactional
    public void assignResource(Long resourceId, Long requestId, Double quantity) {
        Resource resource = resourceRepository.findById(resourceId).orElseThrow(
                () -> new ResourceNotFound("Resource with id " + resourceId + " not found"));

        if (resource.isExpired()) {
            throw new ResourceExpiredError("Resource with id " + resourceId + " has expired.");
        }

        if (resource.isDamaged()) {
            throw new ResourceDamagedError("Resource with id " + resourceId + " is damaged.");
        }

        if (resource.getQuantity() < quantity) {
            throw new InsufficientResourceException("Not enough quantity to assign resource with id "
                    + resourceId + ", Available: " + resource.getQuantity() + ", Requested: " + quantity);
        } else if (resource.getQuantity().equals(quantity)) {
            resource.fullyAssigned();
        } else {
            resource.makeAvailable();
        }

        ResourceAssignment assignment = new ResourceAssignment(resource, requestId, quantity);
        resourceAssignmentRepository.save(assignment);

        resource.setQuantity(resource.getQuantity() - quantity);
        resourceRepository.save(resource);
    }

    @Transactional
    public void revokeAssignment(Long assignmentId) {
        ResourceAssignment resourceAssignment = resourceAssignmentRepository.findById(assignmentId).orElseThrow(
                () -> new ResourceAssignmentNotFound("Assignment with id " + assignmentId +" not found"));

        Resource resource = resourceAssignment.getResource();

        resource.setQuantity(resource.getQuantity() + resourceAssignment.getAssignedQuantity());
        resource.makeAvailable();
        resourceRepository.save(resource);

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
                        .map(value -> {
                            try {
                                return ResourceType.valueOf(value.toUpperCase());
                            } catch (IllegalArgumentException e) {
                                throw new InvalidArgument("No resource type with name: " + value);
                            }
                        })
                        .toList();
                predicates.add(root.get("resourceType").in(resourceTypes));
            }
            if (organisationId != null) {
                predicates.add(criteriaBuilder.equal(root.get("organisationId"), organisationId));
            }
            if (resourceStatusValues != null && !resourceStatusValues.isEmpty()) {
                List<ResourceStatus> resourceStatuses = resourceStatusValues.stream()
                        .map(value -> {
                            try {
                                return ResourceStatus.valueOf(value.toUpperCase());
                            } catch (IllegalArgumentException e) {
                                throw new InvalidArgument("No resource status with name: " + value);
                            }
                        })
                        .toList();
                predicates.add(root.get("status").in(resourceStatuses));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }

    @Override
    public List<String> getResourceTypes() {
        return Arrays.stream(ResourceType.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }
    @Transactional
    public void updateExpiredStatus() {
        LocalDate today = LocalDate.now();
        List<Resource> expiringResources = resourceRepository.findByExpDateBeforeAndStatus(today, ResourceStatus.AVAILABLE);

        for (Resource resource : expiringResources) {
            resource.setStatus(ResourceStatus.EXPIRED);
        }

        resourceRepository.saveAll(expiringResources);
    }

    private boolean requiresExpirationDate(ResourceType type) {
        return switch (type) {
            case FOOD, MEDICAL -> true;
            case TRANSPORT, FINANCIAL, HOUSING, CLOTHING, EQUIPMENT, OTHER -> false;
        };
    }

    public List<ResourceAssignment> getAssignments() {
        return resourceAssignmentRepository.findAll();
    }
}
