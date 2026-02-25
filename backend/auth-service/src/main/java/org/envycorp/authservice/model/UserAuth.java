package org.envycorp.authservice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "auth_users")
public class UserAuth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String username;
    private String password_hash;
    @ManyToOne(fetch = FetchType.EAGER)
    private Role role;
}
