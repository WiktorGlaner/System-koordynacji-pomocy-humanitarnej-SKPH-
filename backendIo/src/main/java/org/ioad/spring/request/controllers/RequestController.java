package org.ioad.spring.request.controllers;

import org.ioad.spring.request.models.Request;
import org.ioad.spring.request.payload.request.UpdateRequestData;
import org.ioad.spring.request.payload.request.newRequestData;
import org.ioad.spring.request.repository.RequestRepository;
import org.ioad.spring.request.services.IRequestService;
import org.ioad.spring.security.postgresql.models.User;
import org.ioad.spring.user.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/request")
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

    @PostMapping( "/{username}/requests/addrequest")
    public ResponseEntity<String> addRequest(@PathVariable String username, @RequestBody newRequestData requestData) {
        if (userService.getUser(username).isPresent()) {
            try {
                requestService.addRequest(requestData.getLatitude(), requestData.getLongitude(),
                        userService.getUser(username).get(), requestData.getResourceType(), requestData.getAmount(),
                        requestData.getDescription(), requestData.getResourceName());
            } catch (Exception exception) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
            }

            return ResponseEntity.ok("Request successfully added");
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @PutMapping("/{username}/requests/changerequest/{id}")
    public ResponseEntity<String> changeRequest(@PathVariable Long id, @RequestBody UpdateRequestData requestData) {
        if (requestService.getRequestById(id) != null) {
            try {
                requestService.changeRequest(id,requestData.getLatitude(),
                        requestData.getLongitude(),requestData.getResourceType(),
                        requestData.getAmount(),requestData.getDescription(), requestData.getResourceName());
                return ResponseEntity.ok("Request successfully changed");
            } catch (Exception exception) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
            }

        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Request not found");
    }

    @DeleteMapping("/{username}/requests/deleterequest/{id}")
    public ResponseEntity<String> deleteRequest(@PathVariable Long id) {
        try {
            if (requestService.deleteRequest(id)) {
                return ResponseEntity.ok("Request successfully deleted");
            }

        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Request not found");
    }
}
