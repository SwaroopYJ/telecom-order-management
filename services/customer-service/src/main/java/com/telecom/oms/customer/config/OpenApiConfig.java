package com.telecom.oms.customer.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customerServiceOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("Telecom OMS - Customer Service API")
                        .description("""
                                Customer management microservice for Telecom Order Management System.

                                Capabilities:
                                - Customer registration
                                - Customer profile management
                                - Customer retrieval and pagination
                                - Customer status lifecycle management
                                - Customer status history auditing
                                - Soft deletion of customers
                                """)
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Telecom OMS Development Team"))
                        .license(new License()
                                .name("Internal Project")));
    }
}