package org.envycorp.productservice.controller;

import lombok.RequiredArgsConstructor;
import org.envycorp.productservice.model.dto.response.ProductResponseDto;
import org.envycorp.productservice.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/public/")
    public Page<ProductResponseDto> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String name) {
        if(name != null && !name.isBlank())
            return productService.getProducts(name, page);
        else
            return productService.getProducts(page);
    }

}
