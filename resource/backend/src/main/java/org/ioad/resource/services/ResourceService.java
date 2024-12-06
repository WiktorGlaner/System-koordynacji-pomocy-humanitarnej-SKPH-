package org.ioad.resource.services;

import jakarta.transaction.Transactional;
import org.ioad.resource.models.ResourceStatus;
import org.ioad.resource.repositories.ResourceRepository;
import org.ioad.resource.models.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;

    @Autowired
    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    public List<Resource> getResource() {
        return resourceRepository.findAll();
    }

    public void addResource(Resource resource) {
        resourceRepository.save(resource);
    }

    public void removeResource(Long resourceId) {
        boolean exists = resourceRepository.existsById(resourceId);
        if (!exists) {
            throw new IllegalStateException("Resource with id " + resourceId + " does not exists" );
        }
        resourceRepository.deleteById(resourceId);
    }

    @Transactional
    public void updateResource(Long resourceId, String location, Long organisationId, ResourceStatus status) {
        Resource resource = resourceRepository.findById(resourceId).orElseThrow(
                () -> new IllegalStateException("resource with " + resourceId + " does not exist"));

        if (location != null &&
                !location.isEmpty() &&
        !Objects.equals(resource.getLocation(), location)) {
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
}
