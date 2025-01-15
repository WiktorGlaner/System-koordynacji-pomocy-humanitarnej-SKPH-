package org.ioad.spring.task;


import org.ioad.spring.request.models.EStatus;
import org.ioad.spring.request.models.Request;
import org.ioad.spring.resource.models.Resource;
import org.ioad.spring.resource.models.ResourceAssignment;
import org.ioad.spring.user.models.UserInfo;

import java.util.List;
import java.util.Optional;

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

    //Integracja z modułem User
    //Bierzemy
    Optional<UserInfo> getUserByUsename(String username);
    void changeActivity(Boolean active, String username);

    //Udostępniamy
    Double calculateAverageGradeForUser(String username);
}
