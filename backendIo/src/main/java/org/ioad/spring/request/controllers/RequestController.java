package org.ioad.spring.request.controllers;

import org.ioad.spring.request.models.Request;
import org.ioad.spring.request.payload.request.UpdateRequestData;
import org.ioad.spring.request.payload.request.newRequestData;
import org.ioad.spring.request.repository.RequestRepository;
import org.ioad.spring.request.services.IRequestService;
import org.ioad.spring.security.postgresql.models.User;
import org.ioad.spring.user.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class RequestController {

    @Autowired
    private IRequestService requestService;
    @Autowired
    private IUserService userService; //ZAMIENIC POZNIEJ NA INT4ERFEJS
    @Autowired
    private RequestRepository requestRepository; //SORRY MORDECZKI ALE BYŁA FUSZERKA MUSIAŁEM ZROBIC ZA WAS BUZIACZKI

    @GetMapping("/requests")
    public ResponseEntity<List<Request>> getAllRequests() {
        List<Request> requests = requestRepository.findAll();
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/{username}/requests")
    public ResponseEntity<List<Request>> getAllRequestsForUser(@PathVariable String username) {
        if (userService.getUser(username).isPresent()) {
            List<Request> requests = requestService.getAllRequestsByReporter(userService.getUser(username).get().getId());
            return ResponseEntity.ok(requests);
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping( "/{username}/requests/addrequest")
    public ResponseEntity<Void> addRequest(@PathVariable String username, @RequestBody newRequestData requestData) {
        if (userService.getUser(username).isPresent()) {
            try {
                requestService.addRequest(requestData.getLatitude(), requestData.getLongitude(),
                        userService.getUser(username).get(), requestData.getResourceType(), requestData.getAmount(),
                        requestData.getDescription());
            } catch (Exception exception) {
                return ResponseEntity.badRequest().build();
            }

            return ResponseEntity.ok().build();
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{username}/requests/changerequest/{id}")
    public ResponseEntity<Void> changeRequest(@PathVariable Long id, @RequestBody UpdateRequestData requestData) {
        if (requestService.getRequestById(id) != null) {
            try {
                requestService.changeRequest(id,requestData.getLatitude(),
                        requestData.getLongitude(),requestData.getResourceType(),
                        requestData.getAmount(),requestData.getDescription());
                return ResponseEntity.ok().build();
            } catch (Exception exception) {
                return ResponseEntity.badRequest().build();
            }

        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{username}/requests/deleterequest/{id}")
    public ResponseEntity<Void> deleteRequest(@PathVariable Long id) {
        try {
            if (requestService.deleteRequest(id)) {
                return ResponseEntity.ok().build();
            }

        } catch (Exception exception) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.notFound().build();
    }

}
