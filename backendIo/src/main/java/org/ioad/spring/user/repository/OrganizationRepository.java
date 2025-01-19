package org.ioad.spring.user.repository;

import org.ioad.spring.auth.models.User;
import org.ioad.spring.user.models.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    Optional<Organization> findByUser(User user);

    Optional<Organization> findById(Long id);
}
