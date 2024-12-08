package org.ioad.spring.user.payload.response;

public class VolunteerDataResponse {
    private String name;
    private String surname;
    private boolean activity;


    public VolunteerDataResponse(String name, String surname, boolean activity) {
        this.name = name;
        this.surname = surname;
        this.activity = activity;
    }

    public boolean isActivity() {
        return activity;
    }

    public void setActivity(boolean activity) {
        this.activity = activity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
}
