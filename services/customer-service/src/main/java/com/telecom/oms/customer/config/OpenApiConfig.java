package com.telecom.oms.customer.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customerServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Telecom OMS - Customer Service API")
                        .description("REST APIs for customer registration, profile management, status lifecycle and soft deletion.")
                        .version("v1.0.0"));
    }
}