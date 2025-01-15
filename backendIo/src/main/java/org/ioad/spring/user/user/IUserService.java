package org.ioad.spring.user.user;

import org.ioad.spring.user.models.UserInfo;
import org.ioad.spring.user.payload.response.OrganizationInfoDataResponse;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    Optional<UserInfo> getUser(String username);
    List<OrganizationInfoDataResponse> getAllOrganizations();
    List<UserInfo> getAllVolunteers();
    void addUserInfo(String username);
    void addOrganizationInfo(String username);
    void changeActivity(Boolean active, String username);
}
