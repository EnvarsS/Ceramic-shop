package org.envycorp.orderservice.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderResponseDto {
    private Long id;
    private Long userId;
    private String status;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;
    private List<OrderItemResponseDto> orderItems;
}
