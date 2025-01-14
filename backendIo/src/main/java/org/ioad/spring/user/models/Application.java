package org.ioad.spring.user.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table( name = "application",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "id"),
        })
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "OrganizationId", nullable = true)
    private Organization organization;

    @ManyToOne
    @JoinColumn(name = "UserInfoID", nullable = true)
    private UserInfo userInfo;

    public Application() {
    }

    public Application(UserInfo userInfo, Organization organization) {
        this.organization = organization;
        this.userInfo = userInfo;
    }
}