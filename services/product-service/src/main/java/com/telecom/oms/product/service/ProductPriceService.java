
package com.telecom.oms.product.service;

import com.telecom.oms.product.dto.CreateProductPriceRequest;
import com.telecom.oms.product.dto.ProductPriceResponse;
import com.telecom.oms.product.entity.Product;
import com.telecom.oms.product.entity.ProductPrice;
import com.telecom.oms.product.exception.ProductNotFoundException;
import com.telecom.oms.product.repository.ProductPriceRepository;
import com.telecom.oms.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductPriceService {

    private final ProductPriceRepository priceRepository;
    private final ProductRepository productRepository;

    public ProductPriceService(
            ProductPriceRepository priceRepository,
            ProductRepository productRepository) {
        this.priceRepository = priceRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public ProductPriceResponse createPrice(
            Long productId,
            CreateProductPriceRequest request) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(
                        "Product not found with ID: " + productId));

        if (request.validTo() != null
                && !request.validTo().isAfter(request.validFrom())) {
            throw new IllegalArgumentException(
                    "Valid-to date must be after valid-from date");
        }

        ProductPrice price = new ProductPrice();
        price.setProduct(product);
        price.setAmount(request.amount());
        price.setCurrency(request.currency());
        price.setBillingFrequency(request.billingFrequency());
        price.setValidFrom(request.validFrom());
        price.setValidTo(request.validTo());

        ProductPrice savedPrice = priceRepository.save(price);

        return toResponse(savedPrice);
    }

    @Transactional(readOnly = true)
    public List<ProductPriceResponse> getPricesByProductId(Long productId) {

        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException(
                    "Product not found with ID: " + productId);
        }

        return priceRepository.findByProductId(productId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ProductPriceResponse toResponse(ProductPrice price) {
        return new ProductPriceResponse(
                price.getId(),
                price.getProduct().getId(),
                price.getProduct().getSku(),
                price.getAmount(),
                price.getCurrency(),
                price.getBillingFrequency(),
                price.getValidFrom(),
                price.getValidTo(),
                price.getCreatedAt()
        );
    }
}
