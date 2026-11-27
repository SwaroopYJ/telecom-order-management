
package com.telecom.oms.product.service;

import com.telecom.oms.product.dto.CreateProductRequest;
import com.telecom.oms.product.dto.ProductResponse;
import com.telecom.oms.product.entity.Product;
import com.telecom.oms.product.entity.ProductCategory;
import com.telecom.oms.product.enums.ProductStatus;
import com.telecom.oms.product.exception.DuplicateProductException;
import com.telecom.oms.product.exception.ProductNotFoundException;
import com.telecom.oms.product.mapper.ProductMapper;
import com.telecom.oms.product.repository.ProductCategoryRepository;
import com.telecom.oms.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductCategoryRepository categoryRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private ProductCategory category;
    private Product product;
    private ProductResponse response;

    @BeforeEach
    void setUp() {
        category = new ProductCategory();
        category.setCode("BROADBAND");
        category.setName("Broadband");

        product = new Product();
        product.setSku("BB-2001");
        product.setName("Fibre Broadband 200 Mbps");
        product.setCategory(category);
        product.setStatus(ProductStatus.ACTIVE);

        response = new ProductResponse(
                1L, "BB-2001", "Fibre Broadband 200 Mbps",
                null, "BROADBAND", "Broadband",
                ProductStatus.ACTIVE, null, null
        );
    }

    @Test
    void createProductSuccessfully() {
        CreateProductRequest request = new CreateProductRequest(
                "BB-2001", "Fibre Broadband 200 Mbps",
                "200 Mbps broadband plan", "BROADBAND"
        );

        when(productRepository.existsBySku("BB-2001")).thenReturn(false);
        when(categoryRepository.findByCode("BROADBAND"))
                .thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(response);

        ProductResponse result = productService.createProduct(request);

        assertNotNull(result);
        assertEquals("BB-2001", result.sku());
        assertEquals(ProductStatus.ACTIVE, result.status());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void createProductShouldRejectDuplicateSku() {
        CreateProductRequest request = new CreateProductRequest(
                "BB-1001", "Duplicate Product",
                "Duplicate test", "BROADBAND"
        );

        when(productRepository.existsBySku("BB-1001")).thenReturn(true);

        assertThrows(
                DuplicateProductException.class,
                () -> productService.createProduct(request)
        );

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void createProductShouldRejectUnknownCategory() {
        CreateProductRequest request = new CreateProductRequest(
                "BB-3001", "Test Product",
                "Unknown category test", "UNKNOWN"
        );

        when(productRepository.existsBySku("BB-3001")).thenReturn(false);
        when(categoryRepository.findByCode("UNKNOWN"))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> productService.createProduct(request)
        );

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void getProductByIdSuccessfully() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toResponse(product)).thenReturn(response);

        ProductResponse result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals("BB-2001", result.sku());
        verify(productRepository).findById(1L);
    }

    @Test
    void getProductByIdShouldThrowWhenNotFound() {
        when(productRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> productService.getProductById(9999L)
        );

        verify(productMapper, never()).toResponse(any(Product.class));
    }
}
