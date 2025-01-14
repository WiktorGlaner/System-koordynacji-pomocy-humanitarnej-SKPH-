package org.ioad.spring.user.controllers;

import jakarta.validation.Valid;
import org.ioad.spring.security.postgresql.payload.response.MessageResponse;
import org.ioad.spring.user.payload.request.FillDataRequest;
import org.ioad.spring.user.payload.request.OrganizationDataRequest;
import org.ioad.spring.user.payload.response.OrganizationInfoDataResponse;
import org.ioad.spring.user.payload.response.UserInfoDataResponse;
import org.ioad.spring.user.payload.response.VolunteerDataResponse;
import org.ioad.spring.user.models.Organization;
import org.ioad.spring.user.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

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

    @PreAuthorize("hasRole('ROLE_VOLUNTEER') || hasRole('ROLE_VICTIM') || hasRole('ROLE_AUTHORITY') || hasRole('ROLE_DONOR')")
    @GetMapping("/getUserInfo")
    public ResponseEntity<UserInfoDataResponse> getUserInfo() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserInfoDataResponse userInfoDataResponse = userService.getUserInfo(username);
        return ResponseEntity.ok(userInfoDataResponse);
    }

    @PreAuthorize("hasRole('ROLE_ORGANIZATION')" )
    @GetMapping("/getOrganizationInfo")
    public ResponseEntity<OrganizationInfoDataResponse> getOrganizationInfo() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        OrganizationInfoDataResponse organizationInfoDataReponse = userService.getOrganizationInfo(username);
        return ResponseEntity.ok(organizationInfoDataReponse);
    }

    @PreAuthorize("hasRole('ROLE_ORGANIZATION')" )
    @PostMapping("/uploadOrganizationData")
    public ResponseEntity<MessageResponse> fillOrganizationInformation(@Valid @RequestBody OrganizationDataRequest request) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.fillOrganizationInformation(username, request);
        return ResponseEntity.ok(new MessageResponse("Successfully added information about organization"));
    }

//    @PostMapping("/uploadAuthorityData")
//    public ResponseEntity<String> fillAuthorityInformation(@RequestParam String username,
//                                                                    @RequestBody AuthorityDataRequest request) {
//        userService.fillAuthorityInformation(username, request);
//        return ResponseEntity.ok("Successfully added information about authority");
//    }

    @PreAuthorize("hasRole('ROLE_VOLUNTEER') || hasRole('ROLE_VICTIM') || hasRole('ROLE_AUTHORITY') || hasRole('ROLE_DONOR')")
    @PostMapping("/uploadUserData")
    public ResponseEntity<MessageResponse> fillUserInformation(@Valid @RequestBody FillDataRequest request) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.fillUserInformation(username, request);
        return ResponseEntity.ok(new MessageResponse("Successfully added information about user"));
    }

    @PostMapping("/makeApplication")
    public ResponseEntity<String> makeApplication(@RequestBody Long id) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.makeApplication(username, id);
        return ResponseEntity.ok("Successfully added made application");
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MessageResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");

        MessageResponse messageResponse = new MessageResponse(errorMessage);
        return ResponseEntity.badRequest().body(messageResponse);
    }
}