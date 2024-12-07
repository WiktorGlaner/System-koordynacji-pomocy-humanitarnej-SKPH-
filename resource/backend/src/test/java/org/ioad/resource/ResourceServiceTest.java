package org.ioad.resource;

import org.ioad.resource.exceptions.InsufficientResourceException;
import org.ioad.resource.exceptions.ResourceAssignmentNotFound;
import org.ioad.resource.exceptions.ResourceExpiredError;
import org.ioad.resource.exceptions.ResourceNotFound;
import org.ioad.resource.models.*;
import org.ioad.resource.repositories.ResourceAssignmentRepository;
import org.ioad.resource.repositories.ResourceRepository;
import org.ioad.resource.services.ResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ResourceServiceTest {

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private ResourceAssignmentRepository resourceAssignmentRepository;

    @InjectMocks
    private ResourceService resourceService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnAvailableResources() {
        List<Resource> mockResources = List.of(
                new Resource(1L, "Ciasto", "Location1", LocalDate.of(2025, 12, 20), 100.0, "pcs", 2L, ResourceType.FOOD),
                new Resource(2L, "Kurtka", "Location2", LocalDate.of(2025, 12, 20), 1.0, "pcs", 2L, ResourceType.CLOTHING)
        );

        when(resourceRepository.findByStatus(ResourceStatus.AVAILABLE)).thenReturn(mockResources);

        List<Resource> availableResources = resourceService.getAvailableResources();

        assertEquals(2, availableResources.size());
    }

    @Test
    void shouldModifyResource() {
        Resource resource =  new Resource(1L, "Ciasto", "Location1", LocalDate.of(2025, 12, 20), 100.0, "pcs", 2L, ResourceType.FOOD);
        Long resourceId = resource.getId();

        when(resourceRepository.findById(resourceId)).thenReturn(java.util.Optional.of(resource));

        resourceService.modifyResource(resourceId, "New Location", 2L, ResourceStatus.UNAVAILABLE);

        assertEquals("New Location", resource.getLocation());
        assertEquals(2L, resource.getOrganisationId());
        assertEquals(ResourceStatus.UNAVAILABLE, resource.getStatus());
    }

    @Test
    void shouldAddResource() {
        Resource resource = new Resource(1L, "Ciasto", "Location1", LocalDate.of(2025, 12, 20), 100.0, "pcs", 2L, ResourceType.FOOD);

        resourceService.addResource(resource);

        verify(resourceRepository, times(1)).save(resource);
    }

    @Test
    void shouldRemoveResource() {
        Resource resource = new Resource(1L,"Ciasto", "Location1", LocalDate.of(2025, 12, 20), 100.0, "pcs", 2L, ResourceType.FOOD);
        Long resourceId = resource.getId();

        when(resourceRepository.existsById(resourceId)).thenReturn(true);

        resourceService.removeResource(resourceId);

        verify(resourceRepository, times(1)).deleteById(resourceId);
    }

    @Test
    void shouldThrowExceptionWhenResourceNotFoundOnRemove() {
        Long resourceId = 1L;

        when(resourceRepository.existsById(resourceId)).thenReturn(false);

        assertThrows(ResourceNotFound.class, () -> resourceService.removeResource(resourceId));
    }

    @Test
    void shouldThrowExceptionWhenResourceIsExpired() {
        Resource resource = new Resource(1L, "Ciasto", "Location1", LocalDate.of(2020, 12, 20), 100.0, "pcs", 2L, ResourceType.FOOD);
        resource.setStatus(ResourceStatus.EXPIRED);
        Long resourceId = resource.getId();

        when(resourceRepository.findById(resourceId)).thenReturn(java.util.Optional.of(resource));

        assertThrows(ResourceExpiredError.class, () -> resourceService.assignResource(resourceId, 1L, 50.0));
    }

    @Test
    void shouldThrowExceptionWhenNotEnoughQuantityToAssign() {
        Resource resource = new Resource(1L, "Ciasto", "Location1", LocalDate.of(2025, 12, 20), 100.0, "pcs", 2L, ResourceType.FOOD);
        Long resourceId = resource.getId();

        when(resourceRepository.findById(resourceId)).thenReturn(java.util.Optional.of(resource));
        when(resourceRepository.getTotalAssignedQuantity(resource.getId())).thenReturn(60.0);

        assertThrows(InsufficientResourceException.class, () -> resourceService.assignResource(resourceId, 1L, 50.0));
    }

    @Test
    void shouldAssignResourceWhenQuantityIsSufficient() {
        Resource resource = new Resource(1L, "Ciasto", "Location1", LocalDate.of(2025, 12, 20), 100.0, "pcs", 2L, ResourceType.FOOD);
        Long resourceId = resource.getId();

        when(resourceRepository.findById(resourceId)).thenReturn(java.util.Optional.of(resource));
        when(resourceRepository.getTotalAssignedQuantity(resourceId)).thenReturn(30.0);

        resourceService.assignResource(resourceId, 1L, 50.0);

        verify(resourceAssignmentRepository, times(1)).save(any(ResourceAssignment.class));
    }

    @Test
    void shouldThrowExceptionWhenAssignmentNotFoundOnRevoke() {
        Long assignmentId = 1L;

        when(resourceAssignmentRepository.findById(assignmentId)).thenReturn(java.util.Optional.empty());

        assertThrows(ResourceAssignmentNotFound.class, () -> resourceService.revokeAssignment(assignmentId));
    }

    @Test
    void shouldReturnResourcesByType() {
        Resource foodResource = new Resource(1L, "Ciasto", "Location1", LocalDate.of(2025, 12, 20), 100.0, "pcs", 2L, ResourceType.FOOD);
        Resource clothingResource = new Resource(2L, "Kurtka", "Location2", LocalDate.of(2025, 12, 20), 1.0, "pcs", 2L, ResourceType.CLOTHING);

        when(resourceRepository.getByResourceType(ResourceType.FOOD)).thenReturn(List.of(foodResource));

        List<Resource> resources = resourceService.getResourceByType(ResourceType.FOOD);

        assertEquals(1, resources.size());
        assertEquals(ResourceType.FOOD, resources.get(0).getResourceType());
    }

    @Test
    void shouldReturnAvailableQuantity() {
        Resource resource = new Resource(1L, "Ciasto", "Location1", LocalDate.of(2025, 12, 20), 100.0, "pcs", 2L, ResourceType.FOOD);
        when(resourceRepository.getTotalAssignedQuantity(resource.getId())).thenReturn(30.0);

        double availableQuantity = resourceService.getAvailableQuantity(resource);

        assertEquals(70.0, availableQuantity);
    }

    @Test
    void shouldThrowExceptionWhenResourceDoesNotExistOnModify() {
        Long resourceId = 1L;

        when(resourceRepository.findById(resourceId)).thenReturn(java.util.Optional.empty());

        assertThrows(ResourceNotFound.class, () -> resourceService.modifyResource(resourceId, "New Location", 2L, ResourceStatus.UNAVAILABLE));
    }

    @Test
    void shouldReturnDonationsByDonorId() {
        Donation donation = new Donation(1L, "Ciasto", "Location1", LocalDate.of(2025, 12, 20), 100.0, "pcs", 2L, ResourceType.FOOD, 3L);

        when(resourceRepository.getByDonationDonorId(3L)).thenReturn(List.of(donation));

        List<Donation> donations = resourceService.getByDonationDonorId(3L);

        assertEquals(1, donations.size());
        assertEquals(3L, donations.get(0).getDonorId());
    }
}