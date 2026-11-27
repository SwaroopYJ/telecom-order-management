package com.telecom.oms.order.client;

import com.telecom.oms.order.dto.ProductPriceResponse;
import com.telecom.oms.order.dto.ProductResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class ProductServiceClient {

    private final RestClient restClient;

    public ProductServiceClient(
            RestClient.Builder builder,
            @Value("${services.product.base-url}") String baseUrl) {

        this.restClient = builder
                .baseUrl(baseUrl)
                .build();
    }

    public ProductResponse getProduct(Long productId) {

        return restClient.get()
                .uri("/api/v1/products/{id}", productId)
                .retrieve()
                .body(ProductResponse.class);
    }

    public List<ProductPriceResponse> getProductPrices(Long productId) {

        return restClient.get()
                .uri("/api/v1/products/{productId}/prices", productId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}