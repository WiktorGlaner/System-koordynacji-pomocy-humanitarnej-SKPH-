package org.ioad.spring.user.payload.response;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrganizationInfoDataResponse {
    private String name;
    private Long id;
    private String username;

    public OrganizationInfoDataResponse(Long id, String name, String username) {
        this.id = id;
        this.name = name;
        this.username = username;
    }

    public OrganizationInfoDataResponse(String name) {
        this.name = name;
    }

}
