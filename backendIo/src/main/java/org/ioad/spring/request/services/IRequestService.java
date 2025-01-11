package org.ioad.spring.request.services;

import org.ioad.spring.request.models.EStatus;
import org.ioad.spring.request.models.Request;
import org.ioad.spring.resource.models.ResourceType;
import org.ioad.spring.user.models.UserInfo;

import java.util.List;

public interface IRequestService {
    public List<Request> getAllRequests();
    public Request getRequestById(Long requestId);
    public Request addRequest(Double latitude, Double longitude, UserInfo reporter,
                              ResourceType resourceType, Integer amount, String description, String resourceName);
    public boolean deleteRequest(Long requestId);
    public boolean deleteAllRequests();
    public List<Request> getAllRequestsByReporter(Long reporterId);
    public List<Request> getAllRequestsByStatus(EStatus status);
    public List<Request> findByStatus(EStatus status);
    public List<List<Double>> getAllLocations();
    public List<Double> getLocation(Long requestId);
    public Request changeRequest(Long requestId, Double newLatitude, Double newLongitude,
                                 ResourceType newResourceType, Integer newAmount, String newDescription, String resourceName);
    public Request changeRequestStatus(Long requestId, EStatus newStatus);
}
