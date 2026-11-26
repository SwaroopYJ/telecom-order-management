
package com.telecom.oms.customer.service;

import com.telecom.oms.customer.dto.CustomerCreateRequest;
import com.telecom.oms.customer.dto.CustomerResponse;
import com.telecom.oms.customer.entity.Customer;
import com.telecom.oms.customer.exception.CustomerAlreadyExistsException;
import com.telecom.oms.customer.exception.CustomerNotFoundException;
import com.telecom.oms.customer.mapper.CustomerMapper;
import com.telecom.oms.customer.repository.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.telecom.oms.customer.dto.CustomerUpdateRequest;
import java.security.SecureRandom;
import java.util.Locale;
import com.telecom.oms.customer.dto.CustomerStatusUpdateRequest;
import com.telecom.oms.customer.entity.CustomerStatus;
import com.telecom.oms.customer.exception.InvalidCustomerStatusTransitionException;
import java.time.LocalDateTime;
@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    private static final SecureRandom RANDOM = new SecureRandom();

    public CustomerService(
            CustomerRepository customerRepository,
            CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    @Transactional
    public CustomerResponse createCustomer(CustomerCreateRequest request) {

        String email = request.email().trim().toLowerCase(Locale.ROOT);
        String phoneNumber = request.phoneNumber().trim();

        if (customerRepository.existsByEmail(email)) {
            throw new CustomerAlreadyExistsException("Email is already registered");
        }

        if (customerRepository.existsByPhoneNumber(phoneNumber)) {
            throw new CustomerAlreadyExistsException("Phone number is already registered");
        }

        Customer customer = customerMapper.toEntity(request);

        customer.setFirstName(request.firstName().trim());
        customer.setLastName(request.lastName().trim());
        customer.setEmail(email);
        customer.setPhoneNumber(phoneNumber);
        customer.setAccountNumber(generateAccountNumber());

        Customer savedCustomer = customerRepository.save(customer);

        return customerMapper.toResponse(savedCustomer);
    }

    private String generateAccountNumber() {

        for (int attempt = 0; attempt < 10; attempt++) {

            int randomNumber = RANDOM.nextInt(100_000_000);

            String accountNumber = String.format(
                    Locale.ROOT,
                    "GB%08d",
                    randomNumber
            );

            if (!customerRepository.existsByAccountNumber(accountNumber)) {
                return accountNumber;
            }
        }

        throw new IllegalStateException(
                "Unable to generate a unique customer account number"
        );
    }

    @Transactional(readOnly = true)
    public CustomerResponse getCustomerByAccountNumber(String accountNumber) {

        Customer customer = customerRepository.findByAccountNumberAndDeletedAtIsNull(accountNumber)
                .orElseThrow(() -> new CustomerNotFoundException(
                        "Customer not found with account number: " + accountNumber
                ));

        return customerMapper.toResponse(customer);
    }

    @Transactional(readOnly = true)
    public Page<CustomerResponse> getAllCustomers(Pageable pageable) {

        return customerRepository.findAllByDeletedAtIsNull(pageable)
                .map(customerMapper::toResponse);
    }
    @Transactional
    public CustomerResponse updateCustomer(
            String accountNumber,
            CustomerUpdateRequest request) {

        Customer customer = customerRepository.findByAccountNumberAndDeletedAtIsNull(accountNumber)
                .orElseThrow(() -> new CustomerNotFoundException(
                        "Customer not found with account number: " + accountNumber));

        String email = request.email().trim().toLowerCase(Locale.ROOT);
        String phoneNumber = request.phoneNumber().trim();

        if (!customer.getEmail().equals(email)
                && customerRepository.existsByEmail(email)) {
            throw new CustomerAlreadyExistsException(
                    "Email is already registered");
        }

        if (!customer.getPhoneNumber().equals(phoneNumber)
                && customerRepository.existsByPhoneNumber(phoneNumber)) {
            throw new CustomerAlreadyExistsException(
                    "Phone number is already registered");
        }

        customerMapper.updateEntity(customer, request);

        Customer updatedCustomer = customerRepository.save(customer);

        return customerMapper.toResponse(updatedCustomer);
    }

    @Transactional
    public CustomerResponse updateCustomerStatus(
            String accountNumber,
            CustomerStatusUpdateRequest request) {

        Customer customer = customerRepository.findByAccountNumberAndDeletedAtIsNull(accountNumber)
                .orElseThrow(() -> new CustomerNotFoundException(
                        "Customer not found with account number: " + accountNumber));

        CustomerStatus currentStatus = customer.getStatus();
        CustomerStatus requestedStatus = request.status();

        if (currentStatus == requestedStatus) {
            return customerMapper.toResponse(customer);
        }

        boolean isAllowed = switch (currentStatus) {
            case ACTIVE -> requestedStatus == CustomerStatus.SUSPENDED
                    || requestedStatus == CustomerStatus.INACTIVE;

            case SUSPENDED -> requestedStatus == CustomerStatus.ACTIVE
                    || requestedStatus == CustomerStatus.INACTIVE;

            case INACTIVE -> false;
        };

        if (!isAllowed) {
            throw new InvalidCustomerStatusTransitionException(
                    "Cannot change customer status from "
                            + currentStatus + " to " + requestedStatus);
        }

        customer.setStatus(requestedStatus);

        Customer updatedCustomer = customerRepository.save(customer);

        return customerMapper.toResponse(updatedCustomer);
    }

    @Transactional
    public void deleteCustomer(String accountNumber) {
        Customer customer = customerRepository
                .findByAccountNumberAndDeletedAtIsNull(accountNumber)
                .orElseThrow(() -> new CustomerNotFoundException(
                        "Customer not found with account number: " + accountNumber
                ));

        customer.setDeletedAt(LocalDateTime.now());

        customerRepository.save(customer);
    }
}
