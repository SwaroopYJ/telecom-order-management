
package com.telecom.oms.product.service;

import com.telecom.oms.product.dto.CreateProductPriceRequest;
import com.telecom.oms.product.dto.ProductPriceResponse;
import com.telecom.oms.product.entity.Product;
import com.telecom.oms.product.entity.ProductPrice;
import com.telecom.oms.product.enums.BillingFrequency;
import com.telecom.oms.product.exception.ProductNotFoundException;
import com.telecom.oms.product.repository.ProductPriceRepository;
import com.telecom.oms.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductPriceServiceTest {

    @Mock
    private ProductPriceRepository priceRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductPriceService priceService;

    private Product product;
    private CreateProductPriceRequest request;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setSku("BB-1001");

        request = new CreateProductPriceRequest(
                new BigDecimal("29.99"),
                "GBP",
                BillingFrequency.MONTHLY,
                LocalDateTime.of(2026, 11, 27, 0, 0),
                null
        );
    }

    @Test
    void createPriceSuccessfully() {
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(priceRepository.save(any(ProductPrice.class)))
                .thenAnswer(invocation -> {
                    ProductPrice price = invocation.getArgument(0);
                    return price;
                });

        ProductPriceResponse result =
                priceService.createPrice(1L, request);

        assertNotNull(result);
        assertEquals(new BigDecimal("29.99"), result.amount());
        assertEquals("GBP", result.currency());
        assertEquals(BillingFrequency.MONTHLY, result.billingFrequency());

        verify(priceRepository).save(any(ProductPrice.class));
    }

    @Test
    void createPriceShouldThrowWhenProductNotFound() {
        when(productRepository.findById(9999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> priceService.createPrice(9999L, request)
        );

        verify(priceRepository, never()).save(any(ProductPrice.class));
    }

    @Test
    void createPriceShouldRejectInvalidValidityPeriod() {
        CreateProductPriceRequest invalidRequest =
                new CreateProductPriceRequest(
                        new BigDecimal("39.99"),
                        "GBP",
                        BillingFrequency.MONTHLY,
                        LocalDateTime.of(2026, 12, 10, 0, 0),
                        LocalDateTime.of(2026, 12, 1, 0, 0)
                );

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        assertThrows(
                IllegalArgumentException.class,
                () -> priceService.createPrice(1L, invalidRequest)
        );

        verify(priceRepository, never()).save(any(ProductPrice.class));
    }

    @Test
    void getPricesByProductIdSuccessfully() {
        ProductPrice price = new ProductPrice();
        price.setProduct(product);
        price.setAmount(new BigDecimal("29.99"));
        price.setCurrency("GBP");
        price.setBillingFrequency(BillingFrequency.MONTHLY);

        when(productRepository.existsById(1L)).thenReturn(true);
        when(priceRepository.findByProductId(1L))
                .thenReturn(List.of(price));

        List<ProductPriceResponse> result =
                priceService.getPricesByProductId(1L);

        assertEquals(1, result.size());
        assertEquals(new BigDecimal("29.99"), result.get(0).amount());
        assertEquals("GBP", result.get(0).currency());
    }
}
