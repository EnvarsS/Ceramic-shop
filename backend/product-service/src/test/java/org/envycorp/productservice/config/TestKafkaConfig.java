package org.envycorp.productservice.config;

import org.envycorp.productservice.service.KafkaProducerService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestKafkaConfig {

    @Bean
    @Primary
    public KafkaProducerService kafkaProducerService() {
        return Mockito.mock(KafkaProducerService.class);
    }
}
