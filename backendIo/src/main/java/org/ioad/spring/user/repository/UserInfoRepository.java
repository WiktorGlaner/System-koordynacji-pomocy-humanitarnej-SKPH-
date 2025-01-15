package org.ioad.spring.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ioad.spring.security.postgresql.models.User;
import org.springframework.stereotype.Repository;

import org.ioad.spring.user.models.UserInfo;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
    Optional<UserInfo> findByUser(User user);
}
