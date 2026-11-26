package com.telecom.oms.customer.exception;

import com.telecom.oms.customer.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler =
            new GlobalExceptionHandler();

    @Test
    void handleDataIntegrityViolation_shouldReturnConflict() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getRequestURI())
                .thenReturn("/api/v1/customers");

        DataIntegrityViolationException exception =
                new DataIntegrityViolationException("Duplicate entry");

        // Act
        ResponseEntity<ApiError> response =
                exceptionHandler.handleDataIntegrityViolation(exception, request);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getBody().status());
        assertEquals(
                "A customer with the provided unique details already exists",
                response.getBody().message()
        );
        assertEquals("/api/v1/customers", response.getBody().path());
    }

}