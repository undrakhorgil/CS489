package edu.miu.cs.cs489.lab6.adsdentalsurgerycli.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, unique = true, length = 64)
    private String username;

    /** Plain demo secret in this lab CLI. Nullable in DB so Hibernate can add the column on legacy {@code users} rows; backfilled at startup. */
    @Column(nullable = true, length = 128)
    private String password;

    @Column(length = 120)
    private String email;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private AdsRole role;
}
