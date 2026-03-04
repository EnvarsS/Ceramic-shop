package org.envycorp.authservice;

import org.envycorp.authservice.config.TestKafkaConfig;
import org.envycorp.authservice.service.KafkaProducerService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestKafkaConfig.class)
class AuthServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
