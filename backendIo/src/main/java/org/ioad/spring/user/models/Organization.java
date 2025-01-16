package org.ioad.spring.user.models;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.ioad.spring.security.postgresql.models.User;

import java.util.List;


@Entity
@Table(name = "organizations",
uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
})
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@NotBlank
    @Column(name = "name")
    private String name;

    @OneToOne
    @JoinColumn(name = "username", referencedColumnName = "username", nullable = false)
    private User user;

    //cascade oznacza ze jesli w jednej tabeli cos usuniemy to i w drugiej tez
    //lazy polaczenie utworzy sie dopiero jesli wywolamy je w kodzie
    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<UserInfo> volunteers;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<UserInfo> getVolunteers() {
        return volunteers;
    }

    public void setVolunteers(List<UserInfo> volunteers) {
        this.volunteers = volunteers;
    }

    public Organization() {
    }

    public Organization(String name) {
        this.name = name;
    }
}
