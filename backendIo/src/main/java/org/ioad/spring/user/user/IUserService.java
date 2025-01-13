package org.ioad.spring.user.user;

import org.ioad.spring.user.models.Organization;
import org.ioad.spring.user.models.UserInfo;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    Optional<UserInfo> getUser(String username);
    List<Organization> getAllOrganizations();
//    List<UserInfo> getAllVolunteer();
    void addUserInfo(String username);
}
