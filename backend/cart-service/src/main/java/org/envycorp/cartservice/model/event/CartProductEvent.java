package org.envycorp.cartservice.model.event;

import java.math.BigDecimal;

public record CartProductEvent(
        Long userId,
        Long productId,
        BigDecimal price){
}
