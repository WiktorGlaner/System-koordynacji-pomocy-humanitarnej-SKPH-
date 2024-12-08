package org.ioad.spring.task;


import org.ioad.spring.request.models.Request;
import org.ioad.spring.resource.models.Resource;

import java.util.List;

public interface TaskServiceCommunication {
    List<Integer> getAllVolunteers();

    //Integracja z modułem Request
    List<Request> getRequestList();
    Request getRequest(long id);


    //Integracja z modułem Resources
    List<Resource> getResourceList();
    Resource getResource(long id);

    void assignResource(Long resourceId, Long requestId, Double quantity);
}
