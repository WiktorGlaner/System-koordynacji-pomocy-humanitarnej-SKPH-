package org.ioad.spring.user.user;

import org.ioad.spring.user.models.Application;
import org.ioad.spring.user.models.Organization;
import org.ioad.spring.user.models.UserInfo;
import org.ioad.spring.security.postgresql.models.User;
import org.ioad.spring.user.payload.request.FillDataRequest;
import org.ioad.spring.user.payload.request.OrganizationDataRequest;
import org.ioad.spring.user.payload.response.ApplicationDataResponse;
import org.ioad.spring.user.payload.response.OrganizationInfoDataResponse;
import org.ioad.spring.user.payload.response.UserInfoDataResponse;
import org.ioad.spring.user.payload.response.VolunteerDataResponse;
import org.ioad.spring.user.repository.OrganizationRepository;
import org.ioad.spring.user.repository.UserInfoRepository;
import org.ioad.spring.user.repository.ApplicationRepository;
import org.ioad.spring.security.postgresql.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class UserService implements IUserService {

    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ApplicationRepository applicationRepository;

    @Override
    public List<OrganizationInfoDataResponse> getAllOrganizations() {
        List<OrganizationInfoDataResponse> organizationInfoDataResponses = new ArrayList<>();
        List<Organization> organizations = organizationRepository.findAll();

        for (Organization organization : organizations) {
            OrganizationInfoDataResponse response = new OrganizationInfoDataResponse(
                    organization.getId(),
                    organization.getName(),
                    organization.getUser().getUsername()
                );
            organizationInfoDataResponses.add(response);
            }
        return organizationInfoDataResponses;
    }

    @Override
    public List<UserInfo> getAllVolunteers() {
        List<User> users = userRepository.getAllUsersByRole("ROLE_VOLUNTEER");
        List<UserInfo> volunteers = new ArrayList<>();
        for (User user : users) {
            Optional<UserInfo> userInfo = userInfoRepository.findByUser(user);
            userInfo.ifPresent(volunteers::add);
        }
        return volunteers;
    }


    public List<VolunteerDataResponse> getAllVolunteersInfo(Boolean activity) {
        List<VolunteerDataResponse> volunteerDataResponses = new ArrayList<>();
        List<UserInfo> volunteers = this.getAllVolunteers();

        for (UserInfo userInfo : volunteers) {
            if (activity == null || userInfo.isActivity() == activity) {
                VolunteerDataResponse response = new VolunteerDataResponse(
                        userInfo.getName(),
                        userInfo.getSurname(),
                        userInfo.isActivity()
                );
                volunteerDataResponses.add(response);
            }
        }
        return volunteerDataResponses;
    }

    public List<VolunteerDataResponse> getAllVolunteersInfoByOrganizationId(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Organization organization = organizationRepository.findByUser(user).orElseThrow(() -> new RuntimeException("User not found"));

        List<VolunteerDataResponse> volunteerDataResponses = new ArrayList<>();
        List<UserInfo> volunteers = this.getAllVolunteers();

        for (UserInfo userInfo : volunteers) {
            if (userInfo.getOrganization() != null && userInfo.getOrganization().getId() == organization.getId()) {
                VolunteerDataResponse response = new VolunteerDataResponse(
                        userInfo.getUser().getUsername(),
                        userInfo.getPesel(),
                        userInfo.getUser().getEmail(),
                        userInfo.getId(),
                        userInfo.isActivity(),
                        userInfo.getSurname(),
                        userInfo.getName()
                );
                volunteerDataResponses.add(response);
            }
        }
        return volunteerDataResponses;
    }

    @Override
    public Optional<UserInfo> getUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

       return (userInfoRepository.findByUser(user));
    }

    @Override
    public void addOrganizationInfo(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        Organization organization = new Organization();
        organization.setUser(user);
        organizationRepository.save(organization);
    }

    @Override
    public void addUserInfo(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserInfo userInfo = new UserInfo();
        userInfo.setUser(user);
        userInfoRepository.save(userInfo);
    }

    public void makeApplication(String username, Long id){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserInfo userInfo = userInfoRepository.findByUser(user).orElseThrow(() -> new RuntimeException("User not found"));
        Organization organization = organizationRepository.findById(id).orElseThrow(() -> new RuntimeException("Organization not found"));
        Application application = new Application();
        application.setUserInfo(userInfo);
        application.setOrganization(organization);

        applicationRepository.save(application);
    }

    public void fillOrganizationInformation(String username, OrganizationDataRequest request) {
        User user = userRepository.findByUsername(username).orElseThrow(()
                -> new RuntimeException("Organization not found"));

        Optional<Organization> organization = organizationRepository.findByUser(user);
        organization.ifPresent(value -> {
            value.setName(request.getName());
            organizationRepository.save(value);
        });
    }

    public UserInfoDataResponse getUserInfo(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<UserInfo> userInfo = userInfoRepository.findByUser(user);

        return userInfo.map(value ->
                new UserInfoDataResponse(value.getName(), value.getSurname(), value.getPesel())
        ).orElseThrow(() -> new RuntimeException("UserInfo not found for user: " + username));
    }

    public OrganizationInfoDataResponse getOrganizationInfo(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<Organization> organization = organizationRepository.findByUser(user);

        return organization.map(value ->
                new OrganizationInfoDataResponse(value.getName())
        ).orElseThrow(() -> new RuntimeException("UserInfo not found for user: " + username));
    }

    // Przykład metody w serwisie
    public ApplicationDataResponse isApplicationExist(Long organizationId, String username) {
        // Sprawdzamy, czy w bazie danych istnieje aplikacja o danym id organizacji i użytkownika
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserInfo userInfo = userInfoRepository.findByUser(user).orElseThrow(() -> new RuntimeException("User not found"));
        Long userId = userInfo.getId();
        Boolean exitst = applicationRepository.existsByOrganizationIdAndUserInfoId(organizationId, userId);
        ApplicationDataResponse applicationDataResponse = new ApplicationDataResponse(exitst);
        return applicationDataResponse;
    }

    public void fillUserInformation(String username, FillDataRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<UserInfo> userInfo = userInfoRepository.findByUser(user);
        userInfo.ifPresent(value -> {
                    value.setName(request.getName());
                    value.setSurname(request.getSurname());
                    value.setPesel(request.getPesel());
                    userInfoRepository.save(value);
                });
    }

    public void changeActivity(Boolean activity, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<UserInfo> userInfo = userInfoRepository.findByUser(user);
        userInfo.ifPresent(value -> {
            value.setActivity(activity);
            userInfoRepository.save(value);
        });
    }

    @Transactional
    public void deleteApplication(String username, Long id) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserInfo userInfo = userInfoRepository.findByUser(user).orElseThrow(() -> new RuntimeException("User not found"));
        Long userId = userInfo.getId();
        applicationRepository.deleteApplicationByOrganizationIdAndUserInfoId(id, userId);
    }

    public ApplicationDataResponse getApprovalStatus(String username, Long id) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserInfo userInfo = userInfoRepository.findByUser(user).orElseThrow(() -> new RuntimeException("User not found"));
        Long userId = userInfo.getId();
        Application approval = applicationRepository.findApprovalByOrganizationIdAndUserInfoId(id, userId);
        if (approval.getApproval() == null) {
            ApplicationDataResponse applicationDataResponse = new ApplicationDataResponse(false);
            applicationDataResponse.setNullExists(true);
            return applicationDataResponse;
        }
        ApplicationDataResponse applicationDataResponse = new ApplicationDataResponse(approval.getApproval());
        return applicationDataResponse;
    }

    public ApplicationDataResponse getApprovalStatus(Long id) {
        Application approval = applicationRepository.findById(id).orElseThrow(() -> new RuntimeException("Application not found"));
        if (approval.getApproval() == null) {
            ApplicationDataResponse applicationDataResponse = new ApplicationDataResponse(false);
            applicationDataResponse.setNullExists(true);
            return applicationDataResponse;
        }
        ApplicationDataResponse applicationDataResponse = new ApplicationDataResponse(approval.getApproval());
        return applicationDataResponse;
    }

    public List<ApplicationDataResponse> getApplicationByOrganizationId(String username) {

        List<ApplicationDataResponse> applicationDataResponses = new ArrayList<>();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Organization organization = organizationRepository.findByUser(user).orElseThrow(() -> new RuntimeException("User not found"));

        List<Application> applications = applicationRepository.findAllByOrganizationId(organization.getId());

        for (Application application : applications) {
            ApplicationDataResponse response = new ApplicationDataResponse(
                    application.getUserInfo().getUser().getUsername(),
                    application.getUserInfo().getUser().getEmail(),
                    application.getUserInfo().getPesel(),
                    application.getUserInfo().getSurname(),
                    application.getUserInfo().getName(),
                    application.getId()
            );
            if (application.getApproval() == null) {
                response.setNullExists(true);
                response.setExists(false);
            } else {
                response.setExists(true);
            }
            applicationDataResponses.add(response);
        }
        System.out.println(applicationDataResponses);
        return applicationDataResponses;
    }

    public void acceptApplication(Long id) {
        Application application = applicationRepository.findById(id).orElseThrow(() -> new RuntimeException("Application not found"));
        application.setApproval(true);
        applicationRepository.save(application);
        application.getUserInfo().setOrganization(application.getOrganization());
        userInfoRepository.save(application.getUserInfo());
    }

    public void rejectApplication(Long id) {
        Application application = applicationRepository.findById(id).orElseThrow(() -> new RuntimeException("Application not found"));
        application.setApproval(false);
        applicationRepository.save(application);
    }

    public void deleteVolunteer(Long id) {
        UserInfo userInfo = userInfoRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        userInfo.setOrganization(null);
        userInfoRepository.save(userInfo);
    }
}
