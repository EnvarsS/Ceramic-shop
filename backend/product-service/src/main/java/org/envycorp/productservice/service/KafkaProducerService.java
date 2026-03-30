package org.envycorp.productservice.service;

import lombok.RequiredArgsConstructor;
import org.envycorp.productservice.model.event.KafkaEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, KafkaEvent> kafkaTemplate;

    public void sendProductToCart(KafkaEvent event) {
        kafkaTemplate.send("cart-product-events", event);
    }

}
