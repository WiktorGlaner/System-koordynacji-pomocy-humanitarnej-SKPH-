package org.ioad.spring.user.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrganizationDataRequest {
    @NotBlank(message = "Surname cannot be empty")
    private String name;

}
