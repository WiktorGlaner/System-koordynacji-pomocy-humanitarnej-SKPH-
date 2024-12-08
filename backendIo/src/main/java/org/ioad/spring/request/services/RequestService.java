package org.ioad.spring.request.services;

import org.ioad.spring.request.models.EStatus;
import org.ioad.spring.request.models.Request;
import org.ioad.spring.request.repository.RequestRepository;
import org.ioad.spring.resource.models.ResourceType;
import org.ioad.spring.user.models.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Service
public class RequestService implements IRequestService {
    @Autowired
    RequestRepository requestRepository;
    @Override
    public List<Request> getAllRequests() {
        return new ArrayList<>(requestRepository.findAll());
    }

    @Override
    public Request getRequestById(Long requestId) {
        Optional<Request> request = requestRepository.findById(requestId);
        return request.orElse(null);

    }

    @Override
    public Request addRequest(Double latitude, Double longitude, UserInfo reporter,
                              ResourceType resourceType, Integer amount, String description) {
        return requestRepository
                .save(new Request(description, latitude, longitude,
        reporter, resourceType, amount));
    }

    @Override
    public boolean deleteRequest(Long requestId) {
        try {
            requestRepository.deleteById(requestId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deleteAllRequests() {
        try {
            requestRepository.deleteAll();
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    @Override
    public List<Request> getAllRequestsByReporter(Long reporterId) {
        return requestRepository.findByReporterId(reporterId);
    }

    @Override
    public List<Request> getAllRequestsByStatus(EStatus status) {
        return requestRepository.findByStatus(status);
    }

    @Override
    public List<Request> findByStatus(EStatus status) {
        return requestRepository.findByStatus(status);
    }

    @Override
    public List<List<Double>> getAllLocations(){
        List<Request> requests = requestRepository.findAll();
        if(requests.isEmpty()) {
            return Collections.emptyList();
        }
        List<List<Double>> locations = new ArrayList<>();
        for(Request request: requests) {
            locations.add(getLocation(request.getRequestId()));
        }
        return locations;
    }

    @Override
    public List<Double> getLocation(Long requestId){
        Request request = requestRepository.findByRequestId(requestId);
        if(request != null) {
            List<Double> location = new ArrayList<>();
            location.add(request.getLatitude());
            location.add(request.getLongitude());
            return location;
        }
        return Collections.emptyList();
    }

    @Override
    public Request changeRequest(Long requestId, Double newLatitude, Double newLongitude,
                                 ResourceType newResourceType, Integer newAmount, String newDescription) {
        Request request = requestRepository.findByRequestId(requestId);
        if (newLatitude != null) {
            request.setLatitude(newLatitude);
        }
        if (newLongitude != null) {
            request.setLongitude(newLongitude);
        }
        // DODAC SPR CZY ISTNIEJE ZASOB O TAKIM ID ALE TO JUZ
        // JAK BEDZIE WIADOMO JAKIE NAZWY METOD Z RESOURCE
        if (newResourceType != null) {
            request.setResourceType(newResourceType);
        }
        if (newAmount != null && newAmount > 0) {
            request.setAmount(newAmount);
        }
        if (newDescription != null) {
            request.setDescription(newDescription);
        }
        return requestRepository.save(request);
    }

    @Override
    public Request changeRequestStatus(Long requestId, EStatus newStatus) {
        Request request = requestRepository.findByRequestId(requestId);
        request.setStatus(newStatus);
        return requestRepository.save(request);
    }
}
