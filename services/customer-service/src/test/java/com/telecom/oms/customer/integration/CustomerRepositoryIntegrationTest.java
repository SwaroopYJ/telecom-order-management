package com.telecom.oms.customer.integration;

import com.telecom.oms.customer.entity.Customer;
import com.telecom.oms.customer.entity.CustomerStatus;
import com.telecom.oms.customer.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Testcontainers
class CustomerRepositoryIntegrationTest {

    @Container
    static final MySQLContainer<?> mysql =
            new MySQLContainer<>("mysql:8.4")
                    .withDatabaseName("oms_customer")
                    .withUsername("test_user")
                    .withPassword("test_password");

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void shouldSaveAndRetrieveCustomerFromMySQL() {
        // Arrange
        Customer customer = new Customer();
        customer.setAccountNumber("GB12345678");
        customer.setFirstName("Integration");
        customer.setLastName("Test");
        customer.setEmail("integration.test@example.com");
        customer.setPhoneNumber("+919876543211");
        customer.setStatus(CustomerStatus.ACTIVE);

        // Act
        Customer savedCustomer = customerRepository.saveAndFlush(customer);

        Optional<Customer> result =
                customerRepository.findByAccountNumberAndDeletedAtIsNull(
                        savedCustomer.getAccountNumber()
                );

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Integration", result.get().getFirstName());
        assertEquals("GB12345678", result.get().getAccountNumber());
    }
}