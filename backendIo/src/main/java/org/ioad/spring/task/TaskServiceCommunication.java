package org.ioad.spring.task;


import org.ioad.spring.request.models.EStatus;
import org.ioad.spring.request.models.Request;
import org.ioad.spring.resource.models.Resource;
import org.ioad.spring.resource.models.ResourceAssignment;

import java.util.List;

public interface TaskServiceCommunication {
    List<Integer> getAllVolunteers();

    //Integracja z modułem Request
    List<Request> getRequestList();
    Request getRequest(long id);
    Request changeRequestStatus(Long requestId, EStatus newStatus);


    //Integracja z modułem Resources
    List<Resource> getResourceList();
    Resource getResource(long id);
    void assignResource(Long resourceId, Long requestId, Double quantity);
    List<ResourceAssignment> getResourcesInTask(Long id);
}
