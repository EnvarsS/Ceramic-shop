package org.envycorp.productservice.service;

import lombok.RequiredArgsConstructor;
import org.envycorp.productservice.model.dto.response.ProductResponseDto;
import org.envycorp.productservice.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
        return productRepository.searchByName(fuzzyQuery, originalQuery.trim(), pageable)
                .map(pr -> modelMapper.map(pr, ProductResponseDto.class));
    }
}
