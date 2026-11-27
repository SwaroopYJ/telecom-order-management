package com.telecom.oms.order.dto;

public record ApiResponse<T>(
        boolean success,
        String message,
        T data
) {
}