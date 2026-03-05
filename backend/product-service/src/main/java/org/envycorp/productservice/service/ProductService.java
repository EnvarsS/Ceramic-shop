package org.envycorp.productservice.service;

import lombok.RequiredArgsConstructor;
import org.envycorp.productservice.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;


}
