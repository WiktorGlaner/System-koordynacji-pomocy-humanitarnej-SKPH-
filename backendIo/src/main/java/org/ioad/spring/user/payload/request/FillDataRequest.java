package org.ioad.spring.user.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FillDataRequest {
    @NotBlank(message = "Name cannot be empty")
    private String name;

    @NotBlank(message = "Surname cannot be empty")
    private String surname;

    @Pattern(
            regexp = "\\d{11}",
            message = "PESEL must consist of exactly 11 digits"
    )
    private String pesel;

}
