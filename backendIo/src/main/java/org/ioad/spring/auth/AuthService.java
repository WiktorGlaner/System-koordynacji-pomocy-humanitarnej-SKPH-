package org.ioad.spring.auth;

import org.ioad.spring.auth.models.ERole;
import org.ioad.spring.auth.models.Role;
import org.ioad.spring.auth.models.User;
import org.ioad.spring.auth.repository.RoleRepository;
import org.ioad.spring.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthService implements IAuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Optional<User> getUserByUsername(String username){
        return userRepository.findByUsername(username);
    }

    @Override
    public Boolean userExistsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public Boolean userExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public List<User> getAllUsersByRole(String role) {
        return userRepository.getAllUsersByRole(role);
    }

    @Override
    public Optional<Role> getRoleByName(ERole name) {
        return roleRepository.findByName(name);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }


}
