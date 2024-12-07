package org.ioad.resource.services;

import org.ioad.resource.models.*;

import java.util.List;

public interface IResourceService {
    List<Donation> getAllDonations();
    List<Resource> getAllResources();
    List<ResourceAssignment> getAllResourceAssignment();
    void modifyResource(Long resourceId, String location, Long organisationId, ResourceStatus status);
    List<Resource> getResourceByType(ResourceType type);
    List<Donation> getByDonationType(ResourceType type);
    List<Donation> getByDonationDonorId(Long donorId);
    List<Resource> getAvailableResources();
    List<ResourceAssignment> getResourceAssignments(Long resourceId);
}
