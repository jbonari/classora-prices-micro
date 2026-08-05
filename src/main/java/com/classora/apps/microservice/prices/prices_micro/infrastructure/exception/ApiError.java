package com.classora.apps.microservice.prices.prices_micro.infrastructure.exception;

import java.time.LocalDateTime;

/**
 * Standard error payload returned by the API.
 */
public record ApiError(
        LocalDateTime timestamp,
        int status,
        String error,
        String message) {

    public static ApiError of(int status, String error, String message) {
        return new ApiError(LocalDateTime.now(), status, error, message);
    }
}
