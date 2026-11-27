
package com.telecom.oms.order.client;

import com.telecom.oms.order.dto.ApiResponse;
import com.telecom.oms.order.dto.CustomerResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class CustomerServiceClient {

    private final RestClient restClient;

    public CustomerServiceClient(
            RestClient.Builder builder,
            @Value("${services.customer.base-url}") String baseUrl) {

        this.restClient = builder
                .baseUrl(baseUrl)
                .build();
    }

    public CustomerResponse getCustomer(String accountNumber) {

        ApiResponse<CustomerResponse> response = restClient.get()
                .uri("/api/v1/customers/{accountNumber}", accountNumber)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        if (response == null || !response.success() || response.data() == null) {
            throw new IllegalStateException(
                    "Unable to retrieve customer: " + accountNumber);
        }

        return response.data();
    }
}
