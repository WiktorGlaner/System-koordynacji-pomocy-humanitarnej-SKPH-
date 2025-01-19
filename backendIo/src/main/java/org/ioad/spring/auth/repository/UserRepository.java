package org.ioad.spring.auth.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.ioad.spring.auth.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);

  Boolean existsByUsername(String username);

  Boolean existsByEmail(String email);


  // String role = "ROLE_*"
  @Query(value = """
          select u.id, u.email, u.password, u.username from users u
          join user_roles as ur on u.id = ur.user_id
          join roles as r on ur.role_id = r.id
          where r.name = ?1""", nativeQuery = true)
  List<User> getAllUsersByRole(String role);

  //List<User> findAllByRoles(Set<Role> roles);
}
