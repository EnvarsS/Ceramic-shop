package org.envycorp.authservice.service;

import lombok.RequiredArgsConstructor;
import org.envycorp.authservice.model.event.AuthEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, AuthEvent> kafkaTemplate;

    public void publishUserRegistered(AuthEvent event) {
        event.setType("REGISTERED");
        kafkaTemplate.send("user-registered", event);
    }

    public void publishUserDeleted(AuthEvent event) {
        event.setType("DELETED");
        kafkaTemplate.send("user-deleted", event);
    }

    public void publishUserUpdated(AuthEvent event) {
        event.setType("UPDATED");
        kafkaTemplate.send("user-updated", event);
    }
}
