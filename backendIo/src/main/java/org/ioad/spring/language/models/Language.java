package org.ioad.spring.language.models;

import jakarta.persistence.*;
import lombok.*;
import org.ioad.spring.auth.models.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "user_languages",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "user_id"),
        })
public class Language {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "language", nullable = false)
    private String language;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;


}
