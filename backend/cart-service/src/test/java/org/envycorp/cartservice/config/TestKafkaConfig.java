package org.envycorp.cartservice.config;

import org.envycorp.cartservice.service.KafkaConsumerService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestKafkaConfig {

    @Bean
    @Primary
    public KafkaConsumerService authEventConsumer() {
        return Mockito.mock(KafkaConsumerService.class);
    }
}
