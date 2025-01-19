package org.ioad.spring.auth;

import org.ioad.spring.auth.models.ERole;
import org.ioad.spring.auth.models.Role;
import org.ioad.spring.auth.models.User;

import java.util.List;
import java.util.Optional;

public interface IAuthService {
    Optional<User> getUserByUsername(String username);
    Boolean userExistsByUsername(String username);
    Boolean userExistsByEmail(String email);
    List<User> getAllUsersByRole(String role);
    Optional<Role> getRoleByName(ERole name);
    User getUserById(Long id);
}
