package org.ioad.resource.repositories;

import org.ioad.resource.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
        @Query("SELECT COALESCE(SUM(ra.assignedQuantity), 0) FROM ResourceAssignment ra WHERE ra.resource.id = :resourceId")
        Double getTotalAssignedQuantity(@Param("resourceId") Long resourceId);

        List<Resource> findByStatus(@Param("status") ResourceStatus status);

        @Query("SELECT d FROM Donation d")
        List<Donation> findAllDonation();

        List<Resource> getByResourceType(@Param("resourceType")ResourceType resourceType);

        @Query("SELECT d FROM Donation d WHERE d.resourceType = :resourceType")
        List<Donation> getByDonationType(@Param("resourceType")ResourceType resourceType);

        @Query("SELECT d FROM Donation d WHERE d.donorId = :donorId")
        List<Donation> getByDonationDonorId(@Param("donorId")Long donorId);
}
