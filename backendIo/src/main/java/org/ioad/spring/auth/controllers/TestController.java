package org.ioad.spring.auth.controllers;//package org.ioad.spring.security.postgresql.controllers;

import org.ioad.spring.auth.IAuthService;
import org.ioad.spring.auth.models.Role;
import org.ioad.spring.auth.models.User;
import org.ioad.spring.auth.repository.UserRepository;
import org.ioad.spring.user.models.UserInfo;
import org.ioad.spring.user.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {
    private final UserService userService;

    @GetMapping("/all")
  public String allAccess() {
    return "Public Content.";
  }

  @GetMapping("/user")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN') or hasRole('VICTIM') " +
          "or hasRole('VOLUNTEER') or hasRole('AUTHORITY') or hasRole('ORGANIZATION') or hasRole('DONOR')")
  public String userAccess() {
    return "User Content.";
  }

  @GetMapping("/mod")
  @PreAuthorize("hasRole('MODERATOR')")
  public String moderatorAccess() {
    return "Moderator Board.";
  }

  @GetMapping("/admin")
  @PreAuthorize("hasRole('ADMIN')")
  public String adminAccess() {
    return "Admin Board.";
  }

    // /getbyrole?role=ROLE_*
    @GetMapping("/getbyrole")
    public StringBuilder getByRole(@RequestParam String role) {
        List<User> users = userRepository.getAllUsersByRole(role);
        StringBuilder retStr = new StringBuilder();
        for (User user : users) {
            Set<Role> userRoles = user.getRoles();
            StringBuilder rolesStr = new StringBuilder();
            for (Role userRole : userRoles) {
                rolesStr.append(userRole.getName()).append(" ");
                retStr.append(user.getUsername()).append(" ").append(user.getId())
                        .append(" ").append(rolesStr).append("\n");
            }

        }
        return retStr;
    }

    private final UserRepository userRepository;
    private final IAuthService authService;
    public TestController(UserRepository userRepository, UserService userService, IAuthService authService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.authService = authService;
    }


    @GetMapping("/allUsers")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/testiauth")
    public ResponseEntity<List<UserInfo>> testIAuth() {
        List<UserInfo> users = userService.getAllVolunteers();
        return ResponseEntity.ok(users);
    }
    @GetMapping("/testiauth2")
    public ResponseEntity<List<User>> testIAuth2() {
        List<User> users = authService.getAllUsersByRole("ROLE_VICTIM");
        return ResponseEntity.ok(users);
    }

    @GetMapping("/testiauth3")
    public ResponseEntity<List<User>> testIAuth3() {
        List<User> users = userRepository.getAllUsersByRole("ROLE_VICTIM");
        return ResponseEntity.ok(users);
    }
}
