package org.ioad.spring.resource.services;

import org.ioad.spring.resource.models.*;
import org.ioad.spring.resource.models.Location;

import java.util.List;

public interface IResourceService {
    List<Donation> getAllDonations();
    List<Resource> getAllResources();
    Resource getResourceById(long id);
    List<ResourceAssignment> getAllResourceAssignment();
    void modifyResource(Long resourceId, String description, Location location, Double quantity, String status);
    List<Resource> getResourceByType(ResourceType type);
    List<Donation> getByDonationType(String type);
    List<Donation> getByDonationDonorId(Long donorId);
    List<Resource> getAvailableResources();
    List<ResourceAssignment> getResourceAssignments(Long resourceId);
    Resource getResourceById(Long resourceId);
    List<ResourceAssignment> getAssignmentsByRequestId(Long requestId);
    List<String> getResourceTypes();
    List<Resource> getSumOfQuantityByTypeAndDonorId(ResourceType type, Double donorId);
}
