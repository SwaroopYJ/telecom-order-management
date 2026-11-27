
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

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    public ProductService(
            ProductRepository productRepository,
            ProductCategoryRepository categoryRepository,
            ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
    }

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {

        if (productRepository.existsBySku(request.sku())) {
            throw new DuplicateProductException(
                    "Product already exists with SKU: " + request.sku());
        }

        ProductCategory category = categoryRepository
                .findByCode(request.categoryCode().toUpperCase())
                .orElseThrow(() -> new ProductNotFoundException(
                        "Category not found: " + request.categoryCode()));

        Product product = new Product();
        product.setSku(request.sku());
        product.setName(request.name());
        product.setDescription(request.description());
        product.setCategory(category);
        product.setStatus(ProductStatus.ACTIVE);

        Product savedProduct = productRepository.save(product);

        return productMapper.toResponse(savedProduct);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(
                        "Product not found with ID: " + id));

        return productMapper.toResponse(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {

        return productRepository.findAll()
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByStatus(ProductStatus status) {

        return productRepository.findByStatus(status)
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategory(String categoryCode) {

        return productRepository.findByCategoryCode(categoryCode.toUpperCase())
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }
}
