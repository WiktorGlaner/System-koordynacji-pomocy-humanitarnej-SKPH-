package org.ioad.spring.user.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.ioad.spring.security.postgresql.models.User;


@Entity
@Table( name = "users_info",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
        })
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "name")
    private String name;

    @NotBlank
    @Column(name = "surname")
    private String surname;

    @NotBlank
    @Size(min = 11, max = 11, message = "PESEL has to have 11 letters")
    @Column(name = "pesel")
    private String pesel;


    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    @NotNull
    private EUserRoles role;

    @Column(name = "position")
    private String position;

    @Column(name = "activity")
    private boolean activity;

    @OneToOne
    @JoinColumn(name = "username", referencedColumnName = "username", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "OrganizationId", nullable = true)
    private Organization organization;

    public UserInfo() {

    }

    public UserInfo(String name, String surname, String pesel, String position, boolean activity) {
        this.name = name;
        this.surname = surname;
        this.pesel = pesel;
        this.position = position;
        this.activity = activity;
    }

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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPesel() {
        return pesel;
    }

    public void setPesel(String pesel) {
        this.pesel = pesel;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public boolean isActivity() {
        return activity;
    }

    public void setActivity(boolean activity) {
        this.activity = activity;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public EUserRoles getRole() {
        return role;
    }

    public void setRole(EUserRoles role) {
        this.role = role;
    }
}