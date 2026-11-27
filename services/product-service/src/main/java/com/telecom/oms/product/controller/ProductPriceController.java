
package com.telecom.oms.product.controller;

import com.telecom.oms.product.dto.CreateProductPriceRequest;
import com.telecom.oms.product.dto.ProductPriceResponse;
import com.telecom.oms.product.service.ProductPriceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products/{productId}/prices")
public class ProductPriceController {

    private final ProductPriceService priceService;

    public ProductPriceController(ProductPriceService priceService) {
        this.priceService = priceService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductPriceResponse createPrice(
            @PathVariable Long productId,
            @Valid @RequestBody CreateProductPriceRequest request) {

        return priceService.createPrice(productId, request);
    }

    @GetMapping
    public List<ProductPriceResponse> getPrices(
            @PathVariable Long productId) {

        return priceService.getPricesByProductId(productId);
    }
}
