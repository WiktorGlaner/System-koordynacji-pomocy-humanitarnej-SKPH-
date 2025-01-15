package org.ioad.spring.user.payload.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FillDataRequest {
    private String name;
    private String surname;
    private String pesel;

}
