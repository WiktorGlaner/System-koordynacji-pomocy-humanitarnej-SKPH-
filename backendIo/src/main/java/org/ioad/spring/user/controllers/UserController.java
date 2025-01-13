package org.ioad.spring.user.controllers;

import org.ioad.spring.user.models.UserInfo;
import org.ioad.spring.user.payload.request.AuthorityDataRequest;
import org.ioad.spring.user.payload.request.FillDataRequest;
import org.ioad.spring.user.payload.request.OrganizationDataRequest;
import org.ioad.spring.user.payload.response.VolunteerDataResponse;
import org.ioad.spring.user.models.Organization;
import org.ioad.spring.user.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/allOrganizations")
    public ResponseEntity<List<Organization>> getAllOrganizations() {
        List<Organization> organizations = userService.getAllOrganizations();
        return ResponseEntity.ok(organizations);
    }

//    @GetMapping("/allVolunteers")
//    public ResponseEntity<List<VolunteerDataResponse>> getAllVolunteers() {
//        List<VolunteerDataResponse> volunteers = userService.getAllVolunteersInfo();
//        return ResponseEntity.ok(volunteers);
//    }

    @GetMapping("/users/{username}")
    public ResponseEntity<Optional<UserInfo>> getUser(@PathVariable String username) {
        Optional<UserInfo> user = userService.getUser(username);
        return ResponseEntity.ok(user);
    }
    

    @PostMapping("/{username}/uploadOrganization-data")
    public ResponseEntity<String> fillOrganizationInformation(@PathVariable String username,
                                                                    @RequestBody OrganizationDataRequest request) {
        userService.fillOrganizationInformation(username, request);
        return ResponseEntity.ok("Successfully added information about organization");
    }

    @PostMapping("/{username}/uploadAuthority-data")
    public ResponseEntity<String> fillAuthorityInformation(@PathVariable String username,
                                                                    @RequestBody AuthorityDataRequest request) {
        userService.fillAuthorityInformation(username, request);
        return ResponseEntity.ok("Successfully added information about authority");
    }

    @PostMapping("/{username}/uploadUser-data")
    public ResponseEntity<String> fillUserInformation(@PathVariable String username,
                                                             @RequestBody FillDataRequest request) {
        userService.fillUserInformation(username, request);
        return ResponseEntity.ok("Successfully added information about user");
    }
}
