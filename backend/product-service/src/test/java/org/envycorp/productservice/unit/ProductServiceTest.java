package org.envycorp.productservice.unit;

import org.envycorp.productservice.exception.NameIsAlreadyTakenException;
import org.envycorp.productservice.exception.ProductNotExistException;
import org.envycorp.productservice.model.dto.request.CreateProductRequestDto;
import org.envycorp.productservice.model.dto.request.PatchProductRequestDto;
import org.envycorp.productservice.model.dto.response.ProductResponseDto;
import org.envycorp.productservice.model.entity.Product;
import org.envycorp.productservice.repository.ProductRepository;
import org.envycorp.productservice.service.KafkaProducerService;
import org.envycorp.productservice.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class ProductServiceTest {
    @Mock
    ProductRepository productRepository;

    @Mock
    ModelMapper modelMapper;

    @Mock
    KafkaProducerService kafkaProducer;

    @InjectMocks
    ProductService productService;

    @Test
    void getProducts_shouldReturnEmptyPage_whenNoProducts() {
        when(productRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        Page<ProductResponseDto> result = productService.getProducts(0);

        assertTrue(result.isEmpty());
        verify(productRepository).findAll(any(Pageable.class));
    }

    @Test
    void getProducts_shouldReturnMappedPage_whenProductsExist() {
        Product p1 = buildProduct(1L, "Mug", "Nice mug", new BigDecimal("18.99"), 10);
        Product p2 = buildProduct(2L, "Plate", "Nice plate", new BigDecimal("24.99"), 5);
        ProductResponseDto dto1 = buildResponseDto(1L, "Mug", new BigDecimal("18.99"));
        ProductResponseDto dto2 = buildResponseDto(2L, "Plate", new BigDecimal("24.99"));

        when(productRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(p1, p2)));
        when(modelMapper.map(p1, ProductResponseDto.class)).thenReturn(dto1);
        when(modelMapper.map(p2, ProductResponseDto.class)).thenReturn(dto2);

        Page<ProductResponseDto> result = productService.getProducts(0);

        assertEquals(2, result.getTotalElements());
        assertEquals("Mug", result.getContent().get(0).getName());
        assertEquals("Plate", result.getContent().get(1).getName());
    }

    @Test
    void getProducts_withSearch_shouldReturnEmptyPage_whenNoMatch() {
        when(productRepository.searchByName(anyString(), anyString(), any(Pageable.class)))
                .thenReturn(Page.empty());

        Page<ProductResponseDto> result = productService.getProducts("nonexistent", 0);

        assertTrue(result.isEmpty());
        verify(productRepository).searchByName(anyString(), anyString(), any(Pageable.class));
    }

    @Test
    void getProducts_withSearch_shouldBuildFuzzyQueryCorrectly() {
        Product p1 = buildProduct(1L, "Ceramic Mug", "Nice", new BigDecimal("18.99"), 10);
        ProductResponseDto dto1 = buildResponseDto(1L, "Ceramic Mug", new BigDecimal("18.99"));

        when(productRepository.searchByName(eq("ceramic mug"), eq("ceramic* mug*"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(p1)));
        when(modelMapper.map(p1, ProductResponseDto.class)).thenReturn(dto1);

        Page<ProductResponseDto> result = productService.getProducts("ceramic mug", 0);

        assertEquals(1, result.getTotalElements());
        verify(productRepository).searchByName(eq("ceramic mug"), eq("ceramic* mug*"), any(Pageable.class));
    }

    @Test
    void getProducts_withSearch_shouldTrimAndBuildFuzzyQuery() {
        when(productRepository.searchByName(eq("mug"), eq("mug*"), any(Pageable.class)))
                .thenReturn(Page.empty());

        productService.getProducts("  mug  ", 0);

        verify(productRepository).searchByName(eq("mug"), eq("mug*"), any(Pageable.class));
    }

    @Test
    void createProduct_shouldThrow_whenNameAlreadyExists() {
        CreateProductRequestDto dto = buildCreateDto("Ceramic Mug", "Nice", new BigDecimal("18.99"), 10);
        when(productRepository.existsByName("Ceramic Mug")).thenReturn(true);

        assertThrows(NameIsAlreadyTakenException.class, () -> productService.createProduct(dto));
        verify(productRepository, never()).save(any());
    }

    @Test
    void createProduct_shouldSave_whenNameIsUnique() {
        CreateProductRequestDto dto = buildCreateDto("New Vase", "Nice vase", new BigDecimal("44.99"), 20);
        Product product = buildProduct(1L, "New Vase", "Nice vase", new BigDecimal("44.99"), 20);

        when(productRepository.existsByName("New Vase")).thenReturn(false);
        when(modelMapper.map(dto, Product.class)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);

        productService.createProduct(dto);

        verify(productRepository).save(product);
    }

    @Test
    void patchProduct_shouldThrow_whenProductNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());
        PatchProductRequestDto dto = new PatchProductRequestDto("New name", null, null);

        assertThrows(ProductNotExistException.class, () -> productService.patchProduct(999L, dto));
        verify(productRepository, never()).save(any());
    }

    @Test
    void patchProduct_shouldUpdateName_whenNameProvided() {
        Product product = buildProduct(1L, "Old Name", "Desc", new BigDecimal("18.99"), 10);
        ProductResponseDto responseDto = buildResponseDto(1L, "New Name", new BigDecimal("18.99"));
        PatchProductRequestDto dto = new PatchProductRequestDto("New Name", null, null);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        when(modelMapper.map(product, ProductResponseDto.class)).thenReturn(responseDto);

        ProductResponseDto result = productService.patchProduct(1L, dto);

        assertEquals("New Name", product.getName());
        assertEquals("New Name", result.getName());
        verify(productRepository).save(product);
    }

    @Test
    void patchProduct_shouldUpdatePrice_whenPriceProvided() {
        Product product = buildProduct(1L, "Mug", "Desc", new BigDecimal("18.99"), 10);
        ProductResponseDto responseDto = buildResponseDto(1L, "Mug", new BigDecimal("29.99"));
        PatchProductRequestDto dto = new PatchProductRequestDto(null, null, new BigDecimal("29.99"));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        when(modelMapper.map(product, ProductResponseDto.class)).thenReturn(responseDto);

        ProductResponseDto result = productService.patchProduct(1L, dto);

        assertEquals(new BigDecimal("29.99"), product.getPrice());
        verify(productRepository).save(product);
    }

    @Test
    void patchProduct_shouldUpdateDescription_whenDescriptionProvided() {
        Product product = buildProduct(1L, "Mug", "Old desc", new BigDecimal("18.99"), 10);
        ProductResponseDto responseDto = buildResponseDto(1L, "Mug", new BigDecimal("18.99"));
        PatchProductRequestDto dto = new PatchProductRequestDto(null, "New desc", null);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        when(modelMapper.map(product, ProductResponseDto.class)).thenReturn(responseDto);

        productService.patchProduct(1L, dto);

        assertEquals("New desc", product.getDescription());
        verify(productRepository).save(product);
    }

    @Test
    void patchProduct_shouldNotChangeFields_whenAllNullProvided() {
        Product product = buildProduct(1L, "Mug", "Desc", new BigDecimal("18.99"), 10);
        ProductResponseDto responseDto = buildResponseDto(1L, "Mug", new BigDecimal("18.99"));
        PatchProductRequestDto dto = new PatchProductRequestDto(null, null, null);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        when(modelMapper.map(product, ProductResponseDto.class)).thenReturn(responseDto);

        productService.patchProduct(1L, dto);

        assertEquals("Mug", product.getName());
        assertEquals("Desc", product.getDescription());
        assertEquals(new BigDecimal("18.99"), product.getPrice());
        verify(productRepository).save(product);
    }

    @Test
    void patchProduct_shouldNotChangeName_whenSameNameProvided() {
        Product product = buildProduct(1L, "Mug", "Desc", new BigDecimal("18.99"), 10);
        ProductResponseDto responseDto = buildResponseDto(1L, "Mug", new BigDecimal("18.99"));
        PatchProductRequestDto dto = new PatchProductRequestDto("Mug", null, null); // same name

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        when(modelMapper.map(product, ProductResponseDto.class)).thenReturn(responseDto);

        productService.patchProduct(1L, dto);

        verify(productRepository).save(product);
        assertEquals("Mug", product.getName());
    }

    @Test
    void deleteProduct_shouldThrow_whenProductNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ProductNotExistException.class, () -> productService.deleteProduct(999L));
        verify(productRepository, never()).delete(any());
    }

    @Test
    void deleteProduct_shouldDelete_whenProductExists() {
        Product product = buildProduct(1L, "Mug", "Desc", new BigDecimal("18.99"), 10);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.deleteProduct(1L);

        verify(productRepository).delete(product);
    }

    private Product buildProduct(Long id, String name, String description, BigDecimal price, int quantity) {
        return new Product(id, name, description, price, quantity);
    }

    private ProductResponseDto buildResponseDto(Long id, String name, BigDecimal price) {
        ProductResponseDto dto = new ProductResponseDto();
        dto.setId(id);
        dto.setName(name);
        dto.setPrice(price);
        return dto;
    }

    private CreateProductRequestDto buildCreateDto(String name, String description, BigDecimal price, int quantity) {
        CreateProductRequestDto dto = new CreateProductRequestDto();
        dto.setName(name);
        dto.setDescription(description);
        dto.setPrice(price);
        dto.setQuantity(quantity);
        return dto;
    }
}
