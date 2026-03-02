package org.envycorp.authservice.model.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthEvent {
    private String type; // "REGISTERED", "DELETED", "UPDATED"
    private Long authId;
    private String username;
    private String email;
}