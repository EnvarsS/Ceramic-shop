package org.envycorp.cartservice.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddCartItemRequestDto {
    @NotNull
    private Long productId;

    @NotNull
    @Min(value = 1, message = "At least 1 amount of product required")
    private Integer quantity;
}
