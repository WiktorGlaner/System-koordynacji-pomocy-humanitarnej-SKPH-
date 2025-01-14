package org.ioad.spring.user.payload.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VolunteerDataResponse {
    private String name;
    private String surname;
    private boolean activity;
    private Long id;
    private String email;
    private String pesel;
    private String username;



    public VolunteerDataResponse(String name, String surname, boolean activity) {
        this.name = name;
        this.surname = surname;
        this.activity = activity;
    }

    public VolunteerDataResponse(String username, String pesel, String email, Long id, boolean activity, String surname, String name) {
        this.username = username;
        this.pesel = pesel;
        this.email = email;
        this.id = id;
        this.activity = activity;
        this.surname = surname;
        this.name = name;
    }
}
