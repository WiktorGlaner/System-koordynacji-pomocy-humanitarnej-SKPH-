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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('ROLE_VOLUNTEER') || hasRole('ROLE_AUTHORITY')" )
    @GetMapping("/allOrganizations")
    public ResponseEntity<List<Organization>> getAllOrganizations() {
        List<Organization> organizations = userService.getAllOrganizations();
        return ResponseEntity.ok(organizations);
    }

    @GetMapping("/allVolunteers")
    public ResponseEntity<List<VolunteerDataResponse>> getAllVolunteers(@RequestParam(name = "activity", required = false) Boolean activity) {
        List<VolunteerDataResponse> volunteers = userService.getAllVolunteersInfo(activity);
        return ResponseEntity.ok(volunteers);
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<Optional<UserInfo>> getUser(@PathVariable String username) {
        Optional<UserInfo> user = userService.getUser(username);
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasRole('ROLE_ORGANIZATION')" )
    @PostMapping("/uploadOrganizationData")
    public ResponseEntity<String> fillOrganizationInformation(@RequestBody OrganizationDataRequest request) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.fillOrganizationInformation(username, request);
        return ResponseEntity.ok("Successfully added information about organization");
    }

//    @PostMapping("/uploadAuthorityData")
//    public ResponseEntity<String> fillAuthorityInformation(@RequestParam String username,
//                                                                    @RequestBody AuthorityDataRequest request) {
//        userService.fillAuthorityInformation(username, request);
//        return ResponseEntity.ok("Successfully added information about authority");
//    }

    @PreAuthorize("hasRole('ROLE_VOLUNTEER') || hasRole('ROLE_VICTIM') || hasRole('ROLE_AUTHORITY') || hasRole('ROLE_DONOR')")
    @PostMapping("/uploadUserData")
    public ResponseEntity<String> fillUserInformation(@RequestBody FillDataRequest request) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.fillUserInformation(username, request);
        return ResponseEntity.ok("Successfully added information about user");
    }

    @PostMapping("/makeApplication")
    public ResponseEntity<String> makeApplication(@RequestBody Long id) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.makeApplication(username, id);
        return ResponseEntity.ok("Successfully added made application");
    }
}