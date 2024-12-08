package org.ioad.spring.request.repository;

import org.ioad.spring.request.models.EStatus;
import org.ioad.spring.request.models.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    Request findByRequestId(Long requestId);

    List<Request> findByReporterId(Long reporterId);

    List<Request> findByStatus(EStatus status);
}
