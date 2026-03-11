package org.envycorp.productservice.service;

import lombok.RequiredArgsConstructor;
import org.envycorp.productservice.exception.NameIsAlreadyTakenException;
import org.envycorp.productservice.exception.ProductNotExistException;
import org.envycorp.productservice.model.dto.request.CreateProductRequestDto;
import org.envycorp.productservice.model.dto.request.PatchProductRequestDto;
import org.envycorp.productservice.model.dto.response.ProductResponseDto;
import org.envycorp.productservice.model.entity.Product;
import org.envycorp.productservice.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final static int PAGE_SIZE = 10;

    public Page<ProductResponseDto> getProducts(int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        return productRepository.findAll(pageable)
                .map(pr -> modelMapper.map(pr, ProductResponseDto.class));
    }

    public Page<ProductResponseDto> getProducts(String originalQuery, int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        String fuzzyQuery = Arrays.stream(originalQuery.trim().split("\\s+"))
                .map(word -> word + "*")
                .collect(Collectors.joining(" "));
        return productRepository.searchByName(originalQuery.trim(), fuzzyQuery, pageable)
                .map(pr -> modelMapper.map(pr, ProductResponseDto.class));
    }

    public ProductResponseDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotExistException("Product with id " + id + " is not existed"));

        return modelMapper.map(product, ProductResponseDto.class);
    }

    @Transactional
    public void createProduct(CreateProductRequestDto createProductRequestDto) {
        if (productRepository.existsByName(createProductRequestDto.getName()))
            throw new NameIsAlreadyTakenException("Product already exists");

        productRepository.save(modelMapper.map(createProductRequestDto, Product.class));
    }

    @Transactional
    public ProductResponseDto patchProduct(Long id, PatchProductRequestDto putProductRequestDto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotExistException("Product not exists"));

        if (putProductRequestDto.getName() != null && !putProductRequestDto.getName().equals(product.getName()))
            product.setName(putProductRequestDto.getName());

        if (putProductRequestDto.getPrice() != null && !putProductRequestDto.getPrice().equals(product.getPrice()))
            product.setPrice(putProductRequestDto.getPrice());

        if (putProductRequestDto.getDescription() != null && !putProductRequestDto.getDescription().equals(product.getDescription()))
            product.setDescription(putProductRequestDto.getDescription());

        Product savedProduct = productRepository.save(product);

        return modelMapper.map(savedProduct, ProductResponseDto.class);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotExistException("Product not exists"));

        productRepository.delete(product);
    }
}
