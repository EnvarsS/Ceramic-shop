package org.envycorp.userservice.service;

import lombok.RequiredArgsConstructor;
import org.envycorp.userservice.model.entity.User;
import org.envycorp.userservice.model.event.AuthEvent;
import org.envycorp.userservice.repository.UserRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthEventConsumer {
    private final UserRepository userRepository;

    @KafkaListener(topics = "user-registered", groupId = "user-service")
    public void onUserRegistered(AuthEvent event) {
        User user = new User();
        user.setId(event.getId());
        user.setUsername(event.getUsername());
        user.setEmail(event.getEmail());
        userRepository.save(user);
    }

    @KafkaListener(topics = "user-deleted", groupId = "user-service")
    public void onUserDeleted(AuthEvent event) {
        userRepository.deleteById(event.getId());
    }

    @KafkaListener(topics = "user-updated", groupId = "user-service")
    public void onUserUpdated(AuthEvent event) {
        userRepository.findById(event.getId()).ifPresent(user -> {
            if (event.getUsername() != null) user.setUsername(event.getUsername());
            if (event.getEmail() != null) user.setEmail(event.getEmail());
            userRepository.save(user);
        });
    }
}
