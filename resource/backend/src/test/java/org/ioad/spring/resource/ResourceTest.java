package org.ioad.spring.resource;

import org.ioad.spring.resource.models.Resource;
import org.ioad.spring.resource.models.ResourceStatus;
import org.ioad.spring.resource.models.ResourceType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ResourceTest {
    @Test
    void shouldInitializeResourceWithAvailableStatus() {
        Resource resource = new Resource("Ciasto", "Location1", LocalDate.of(2025, 12, 20), 100.0, "pcs", 2L, ResourceType.FOOD);

        assertEquals("Ciasto", resource.getName());
        assertEquals("Location1", resource.getLocation());
        assertEquals(100.0, resource.getQuantity());
        assertEquals(ResourceStatus.AVAILABLE, resource.getStatus());
    }

    @Test
    void shouldRestoreResourceStatusToAvailable() {
        Resource resource = new Resource("Ciasto", "Location1", LocalDate.of(2025, 12, 20), 100.0, "pcs", 2L, ResourceType.FOOD);
        resource.outOfComission();

        resource.restoreResource();

        assertEquals(ResourceStatus.AVAILABLE, resource.getStatus());
    }

    @Test
    void shouldSetResourceStatusToUnavailable() {
        Resource resource = new Resource("Kurtka", "Location2", LocalDate.of(2025, 12, 20), 50.0, "pcs", 2L, ResourceType.CLOTHING);

        resource.outOfComission();

        assertEquals(ResourceStatus.UNAVAILABLE, resource.getStatus());
    }

    @Test
    void shouldSetStatusToExpiredWhenResourceIsExpired() {
        Resource resource = new Resource("Ciasto", "Location1", LocalDate.of(2020, 12, 20), 100.0, "pcs", 2L, ResourceType.FOOD);

        boolean isExpired = resource.isExpired();

        assertTrue(isExpired);
        assertEquals(ResourceStatus.EXPIRED, resource.getStatus());
    }

    @Test
    void shouldNotSetStatusToExpiredWhenResourceIsNotExpired() {
        Resource resource = new Resource("Ciasto", "Location1", LocalDate.of(2025, 12, 20), 100.0, "pcs", 2L, ResourceType.FOOD);

        boolean isExpired = resource.isExpired();

        assertFalse(isExpired);
        assertEquals(ResourceStatus.AVAILABLE, resource.getStatus());
    }
}

