
package com.telecom.oms.product.controller;

import com.telecom.oms.product.dto.CreateProductRequest;
import com.telecom.oms.product.dto.ProductResponse;
import com.telecom.oms.product.enums.ProductStatus;
import com.telecom.oms.product.service.ProductService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody CreateProductRequest request) {

        ProductResponse response = productService.createProduct(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(
            @PathVariable Long id) {

        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {

        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ProductResponse>> getProductsByStatus(
            @PathVariable ProductStatus status) {

        return ResponseEntity.ok(productService.getProductsByStatus(status));
    }

    @GetMapping("/category/{categoryCode}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(
            @PathVariable String categoryCode) {

        return ResponseEntity.ok(
                productService.getProductsByCategory(categoryCode));
    }
}
