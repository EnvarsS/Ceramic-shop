package org.envycorp.userservice.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    @Id
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String username;

    private String address;

    private LocalDate birthDate;

    @CreationTimestamp
    private LocalDateTime createdAt;

}
