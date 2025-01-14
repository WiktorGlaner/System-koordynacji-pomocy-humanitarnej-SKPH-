package org.ioad.spring.user.payload.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationDataResponse {
    private boolean exists;
    private boolean nullExists = false;

    public ApplicationDataResponse(boolean exists) {
        this.exists = exists;
    }
}
