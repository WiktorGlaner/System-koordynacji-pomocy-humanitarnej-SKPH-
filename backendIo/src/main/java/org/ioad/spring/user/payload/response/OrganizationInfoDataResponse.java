package org.ioad.spring.user.payload.response;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrganizationInfoDataResponse {
    private String name;

    public OrganizationInfoDataResponse(String name) {
        this.name = name;
    }

}
