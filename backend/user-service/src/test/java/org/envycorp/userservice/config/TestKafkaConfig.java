package org.envycorp.userservice.config;

import org.envycorp.userservice.service.AuthEventConsumer;
import org.envycorp.userservice.repository.UserRepository;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestKafkaConfig {

    @Bean
    @Primary
    public AuthEventConsumer authEventConsumer(UserRepository userRepository) {
        return Mockito.mock(AuthEventConsumer.class);
    }
}
