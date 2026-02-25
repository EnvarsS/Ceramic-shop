package org.envycorp.authservice.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "auth_users")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserAuth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String username;
    private String passwordHash;
    @ManyToOne(fetch = FetchType.EAGER)
    private Role role;
}
