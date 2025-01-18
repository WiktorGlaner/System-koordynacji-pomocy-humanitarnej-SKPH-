package org.ioad.spring.security.postgresql;

import org.ioad.spring.security.postgresql.models.ERole;
import org.ioad.spring.security.postgresql.models.Role;
import org.ioad.spring.security.postgresql.models.User;

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
