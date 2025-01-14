package org.ioad.spring.user.repository;

import org.ioad.spring.user.models.Application;
import org.ioad.spring.user.payload.response.ApplicationDataResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    Optional<Application> findById(Long id);
    Optional<Application> findByOrganizationIdAndUserInfoId(Long organizationId, Long userId);
    boolean existsByOrganizationIdAndUserInfoId(Long organizationId, Long userId);
    void deleteApplicationByOrganizationIdAndUserInfoId(Long organizationId, Long userId);
    Application findApprovalByOrganizationIdAndUserInfoId(Long organizationId, Long userId);
}
