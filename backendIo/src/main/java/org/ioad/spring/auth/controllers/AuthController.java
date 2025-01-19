package org.ioad.spring.auth.controllers;//package org.ioad.spring.security.postgresql.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.ioad.spring.language.ILangService;
import org.ioad.spring.auth.repository.UserRepository;
import org.ioad.spring.user.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.ioad.spring.auth.models.ERole;
import org.ioad.spring.auth.models.Role;
import org.ioad.spring.auth.models.User;
import org.ioad.spring.auth.payload.request.LoginRequest;
import org.ioad.spring.auth.payload.request.SignupRequest;
import org.ioad.spring.auth.payload.response.JwtResponse;
import org.ioad.spring.auth.payload.response.MessageResponse;
import org.ioad.spring.auth.repository.RoleRepository;
import org.ioad.spring.auth.security.jwt.JwtUtils;
import org.ioad.spring.auth.security.services.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    IUserService iUserService;

    @Autowired
    ILangService iLangService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity
                .ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            Map<String, ERole> roleMap = Map.of(
                    "admin", ERole.ROLE_ADMIN,
                    "mod", ERole.ROLE_MODERATOR,
                    "victim", ERole.ROLE_VICTIM,
                    "donor", ERole.ROLE_DONOR,
                    "volunteer", ERole.ROLE_VOLUNTEER,
                    "organization", ERole.ROLE_ORGANIZATION,
                    "authority", ERole.ROLE_AUTHORITY
            );

            strRoles.forEach(role -> {
                ERole eRole = roleMap.getOrDefault(role, ERole.ROLE_USER);
                Role foundRole = roleRepository.findByName(eRole)
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                roles.add(foundRole);
            });
        }
        user.setRoles(roles);
        userRepository.save(user);
        iLangService.addLangRecord("en", user);
        if (roles.stream().anyMatch(role -> role.getName().equals(ERole.ROLE_ORGANIZATION))) {
            iUserService.addOrganizationInfo(signUpRequest.getUsername());
        } else {
            iUserService.addUserInfo(signUpRequest.getUsername());
        }
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}
