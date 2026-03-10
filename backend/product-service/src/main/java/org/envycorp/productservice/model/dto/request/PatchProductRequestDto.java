package org.envycorp.productservice.model.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatchProductRequestDto {
    private String name;

    private String description;

    @Size(min = 1)
    private BigDecimal price;
}
