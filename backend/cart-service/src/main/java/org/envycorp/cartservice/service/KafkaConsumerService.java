package org.envycorp.cartservice.service;

import lombok.RequiredArgsConstructor;
import org.envycorp.cartservice.model.event.CartProductEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {
    private final CartService cartService;

    @KafkaListener(topics = "cart-product-events", groupId = "cart-service")
    public void consumeProductEvent(CartProductEvent cartProductEvent) {
        cartService.addItem(cartProductEvent);
    }
}
