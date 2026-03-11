package org.envycorp.productservice.controller;

import lombok.RequiredArgsConstructor;
import org.envycorp.productservice.model.dto.request.CreateProductRequestDto;
import org.envycorp.productservice.model.dto.request.PatchProductRequestDto;
import org.envycorp.productservice.model.dto.response.ProductResponseDto;
import org.envycorp.productservice.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/public")
    public Page<ProductResponseDto> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String name) {
        if(name != null && !name.isBlank())
            return productService.getProducts(name, page);
        else
            return productService.getProducts(page);
    }

    @GetMapping("/public/{id}")
    public ProductResponseDto getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct(@Validated @RequestBody CreateProductRequestDto createProductRequestDto) {
        productService.createProduct(createProductRequestDto);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponseDto patchProduct(@PathVariable Long id, @Validated @RequestBody PatchProductRequestDto patchProductRequestDto) {
        return productService.patchProduct(id, patchProductRequestDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}
