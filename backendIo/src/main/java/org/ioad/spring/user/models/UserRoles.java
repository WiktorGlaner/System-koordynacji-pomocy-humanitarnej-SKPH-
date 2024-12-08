package org.ioad.spring.user.models;

import jakarta.persistence.*;

@Entity
@Table(name = "user_info_roles")
public class UserRoles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EUserRoles name;

    // Konstruktor, który przyjmuje ERole


    public UserRoles() {
    }

    public UserRoles(EUserRoles name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public EUserRoles getName() {
        return name;
    }

    public void setName(EUserRoles name) {
        this.name = name;
    }
}
