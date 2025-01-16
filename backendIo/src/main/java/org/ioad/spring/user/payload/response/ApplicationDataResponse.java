package org.ioad.spring.user.payload.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationDataResponse {
    private boolean exists;
    private boolean nullExists = false;
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String pesel;
    private String username;
    private Long userId;

    public ApplicationDataResponse(boolean exists) {
        this.exists = exists;
    }

    public ApplicationDataResponse(String username, String email, String pesel, String surname, String name, Long id, Long userId) {
        this.username = username;
        this.email = email;
        this.pesel = pesel;
        this.surname = surname;
        this.name = name;
        this.id = id;
        this.userId = userId;
    }
}
