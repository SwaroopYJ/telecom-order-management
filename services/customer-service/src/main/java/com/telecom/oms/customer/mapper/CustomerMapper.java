package com.telecom.oms.customer.mapper;

import com.telecom.oms.customer.dto.CustomerCreateRequest;
import com.telecom.oms.customer.dto.CustomerResponse;
import com.telecom.oms.customer.entity.Customer;
import org.springframework.stereotype.Component;
import com.telecom.oms.customer.dto.CustomerUpdateRequest;
import java.util.Locale;

@Component
public class CustomerMapper {

    public Customer toEntity(CustomerCreateRequest request) {
        Customer customer = new Customer();

        customer.setFirstName(request.firstName());
        customer.setLastName(request.lastName());
        customer.setEmail(request.email());
        customer.setPhoneNumber(request.phoneNumber());
        customer.setDateOfBirth(request.dateOfBirth());

        return customer;
    }

    public CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getAccountNumber(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getPhoneNumber(),
                customer.getDateOfBirth(),
                customer.getStatus(),
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );
    }

    public void updateEntity(Customer customer, CustomerUpdateRequest request) {
        customer.setFirstName(request.firstName().trim());
        customer.setLastName(request.lastName().trim());
        customer.setEmail(request.email().trim().toLowerCase(Locale.ROOT));
        customer.setPhoneNumber(request.phoneNumber().trim());
        customer.setDateOfBirth(request.dateOfBirth());
    }
}