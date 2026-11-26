
package com.telecom.oms.customer.controller;

import com.telecom.oms.customer.dto.ApiResponse;
import com.telecom.oms.customer.dto.CustomerCreateRequest;
import com.telecom.oms.customer.dto.CustomerResponse;
import com.telecom.oms.customer.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import org.springframework.validation.annotation.Validated;
import com.telecom.oms.customer.dto.PageResponse;
import com.telecom.oms.customer.dto.CustomerUpdateRequest;
import org.springframework.web.bind.annotation.PutMapping;
import com.telecom.oms.customer.dto.CustomerStatusUpdateRequest;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
@Validated
@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CustomerResponse>> createCustomer(
            @Valid @RequestBody CustomerCreateRequest request) {

        CustomerResponse response = customerService.createCustomer(request);

        ApiResponse<CustomerResponse> apiResponse = new ApiResponse<>(
                true,
                "Customer created successfully",
                response
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomerByAccountNumber(
            @PathVariable("accountNumber") String accountNumber) {

        CustomerResponse response =
                customerService.getCustomerByAccountNumber(accountNumber);

        ApiResponse<CustomerResponse> apiResponse = new ApiResponse<>(
                true,
                "Customer retrieved successfully",
                response
        );

        return ResponseEntity.ok(apiResponse);
    }
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CustomerResponse>>> getAllCustomers(
            @RequestParam(name = "page", defaultValue = "0")
            @Min(value = 0, message = "Page number cannot be negative") int page,

            @RequestParam(name = "size", defaultValue = "10")
            @Min(value = 1, message = "Page size must be at least 1")
            @Max(value = 100, message = "Page size cannot exceed 100") int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        Page<CustomerResponse> customers = customerService.getAllCustomers(pageable);

        PageResponse<CustomerResponse> pageResponse =
                PageResponse.from(customers);

        ApiResponse<PageResponse<CustomerResponse>> response =
                new ApiResponse<>(
                        true,
                        "Customers retrieved successfully",
                        pageResponse
                );

        return ResponseEntity.ok(response);
    }
    @PutMapping("/{accountNumber}")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateCustomer(
            @PathVariable("accountNumber") String accountNumber,
            @Valid @RequestBody CustomerUpdateRequest request) {

        CustomerResponse updatedCustomer =
                customerService.updateCustomer(accountNumber, request);

        ApiResponse<CustomerResponse> response = new ApiResponse<>(
                true,
                "Customer updated successfully",
                updatedCustomer
        );

        return ResponseEntity.ok(response);
    }
    @PatchMapping("/{accountNumber}/status")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateCustomerStatus(
            @PathVariable("accountNumber") String accountNumber,
            @Valid @RequestBody CustomerStatusUpdateRequest request) {

        CustomerResponse updatedCustomer =
                customerService.updateCustomerStatus(accountNumber, request);

        ApiResponse<CustomerResponse> response = new ApiResponse<>(
                true,
                "Customer status updated successfully",
                updatedCustomer
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{accountNumber}")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(
            @PathVariable("accountNumber") String accountNumber) {

        customerService.deleteCustomer(accountNumber);

        ApiResponse<Void> response = new ApiResponse<>(
                true,
                "Customer deleted successfully",
                null
        );

        return ResponseEntity.ok(response);
    }
}
