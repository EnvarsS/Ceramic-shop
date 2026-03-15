package org.envycorp.cartservice.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CartResponseDto {
    private Long id;
    private Long customerId;
    private List<CartItemResponseDto> items;
}
