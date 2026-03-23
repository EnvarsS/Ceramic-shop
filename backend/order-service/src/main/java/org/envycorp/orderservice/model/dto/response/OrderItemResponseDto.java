package org.envycorp.orderservice.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderItemResponseDto {
    private Long id;
    private Long productId;
    private Integer quantity;
    private BigDecimal snapshotPrice;
}
