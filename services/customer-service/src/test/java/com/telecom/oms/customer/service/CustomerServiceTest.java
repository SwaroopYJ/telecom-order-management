package com.telecom.oms.customer.service;

import com.telecom.oms.customer.mapper.CustomerMapper;
import com.telecom.oms.customer.repository.CustomerRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.telecom.oms.customer.entity.Customer;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.telecom.oms.customer.exception.CustomerNotFoundException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import com.telecom.oms.customer.dto.CustomerStatusUpdateRequest;
import com.telecom.oms.customer.entity.CustomerStatus;
import com.telecom.oms.customer.exception.InvalidCustomerStatusTransitionException;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void deleteCustomer_shouldSetDeletedAtAndSaveCustomer() {
        // Arrange
        String accountNumber = "GB43415202";

        Customer customer = new Customer();
        customer.setAccountNumber(accountNumber);

        when(customerRepository.findByAccountNumberAndDeletedAtIsNull(accountNumber))
                .thenReturn(Optional.of(customer));

        // Act
        customerService.deleteCustomer(accountNumber);

        // Assert
        assertNotNull(customer.getDeletedAt());

        verify(customerRepository)
                .findByAccountNumberAndDeletedAtIsNull(accountNumber);

        verify(customerRepository).save(customer);
    }

    @Test
    void deleteCustomer_shouldThrowExceptionWhenCustomerNotFound() {
        // Arrange
        String accountNumber = "GB43415202";

        when(customerRepository.findByAccountNumberAndDeletedAtIsNull(accountNumber))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.deleteCustomer(accountNumber)
        );

        verify(customerRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void updateCustomerStatus_shouldAllowActiveToSuspended() {
        // Arrange
        String accountNumber = "GB71290859";

        Customer customer = new Customer();
        customer.setAccountNumber(accountNumber);
        customer.setStatus(CustomerStatus.ACTIVE);

        CustomerStatusUpdateRequest request =
                new CustomerStatusUpdateRequest(CustomerStatus.SUSPENDED);

        when(customerRepository.findByAccountNumberAndDeletedAtIsNull(accountNumber))
                .thenReturn(Optional.of(customer));

        // Act
        customerService.updateCustomerStatus(accountNumber, request);

        // Assert
        assertEquals(CustomerStatus.SUSPENDED, customer.getStatus());

        verify(customerRepository).save(customer);
    }

    @Test
    void updateCustomerStatus_shouldAllowSuspendedToActive() {
        // Arrange
        String accountNumber = "GB71290859";

        Customer customer = new Customer();
        customer.setAccountNumber(accountNumber);
        customer.setStatus(CustomerStatus.SUSPENDED);

        CustomerStatusUpdateRequest request =
                new CustomerStatusUpdateRequest(CustomerStatus.ACTIVE);

        when(customerRepository.findByAccountNumberAndDeletedAtIsNull(accountNumber))
                .thenReturn(Optional.of(customer));

        // Act
        customerService.updateCustomerStatus(accountNumber, request);

        // Assert
        assertEquals(CustomerStatus.ACTIVE, customer.getStatus());

        verify(customerRepository).save(customer);
    }
    @Test
    void updateCustomerStatus_shouldRejectInactiveToActive() {
        // Arrange
        String accountNumber = "GB71290859";

        Customer customer = new Customer();
        customer.setAccountNumber(accountNumber);
        customer.setStatus(CustomerStatus.INACTIVE);

        CustomerStatusUpdateRequest request =
                new CustomerStatusUpdateRequest(CustomerStatus.ACTIVE);

        when(customerRepository.findByAccountNumberAndDeletedAtIsNull(accountNumber))
                .thenReturn(Optional.of(customer));

        // Act & Assert
        assertThrows(
                InvalidCustomerStatusTransitionException.class,
                () -> customerService.updateCustomerStatus(accountNumber, request)
        );

        assertEquals(CustomerStatus.INACTIVE, customer.getStatus());

        verify(customerRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }
    @Test
    void updateCustomerStatus_shouldNotSaveWhenStatusIsUnchanged() {
        // Arrange
        String accountNumber = "GB71290859";

        Customer customer = new Customer();
        customer.setAccountNumber(accountNumber);
        customer.setStatus(CustomerStatus.ACTIVE);

        CustomerStatusUpdateRequest request =
                new CustomerStatusUpdateRequest(CustomerStatus.ACTIVE);

        when(customerRepository.findByAccountNumberAndDeletedAtIsNull(accountNumber))
                .thenReturn(Optional.of(customer));

        // Act
        customerService.updateCustomerStatus(accountNumber, request);

        // Assert
        assertEquals(CustomerStatus.ACTIVE, customer.getStatus());

        verify(customerRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

}