package org.envycorp.productservice.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductRequestDto {
    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    @Min(value = 0, message = "Price must be positive")
    private BigDecimal price;

    @NotNull
    @Min(value = 0, message = "Quantity must be positive")
    private Integer quantity;
}
