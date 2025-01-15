package org.ioad.spring.user.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ioad.spring.security.postgresql.models.User;

@Setter
@Getter
@Entity
@Table( name = "users_info",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
        })
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Size(max = 11)
    @Column(name = "pesel")
    private String pesel;

//    @Column(name = "position")
//    private String position;

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

    public UserInfo(String name, String surname, String pesel, boolean activity) {
        this.name = name;
        this.surname = surname;
        this.pesel = pesel;
//        this.position = position;
        this.activity = activity;
    }

}