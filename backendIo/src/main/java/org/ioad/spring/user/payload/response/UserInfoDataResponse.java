package org.ioad.spring.user.payload.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoDataResponse {
    private String name;
    private String surname;
    private String pesel;

    public UserInfoDataResponse(String name, String surname, String pesel) {
        this.name = name;
        this.surname = surname;
        this.pesel = pesel;
    }

}
