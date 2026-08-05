package com.classora.apps.microservice.prices.prices_micro.domain.exception;

import java.time.LocalDateTime;

/**
 * Raised when no price applies for the given brand, product and instant.
 */
public class PriceNotFoundException extends RuntimeException {

    public PriceNotFoundException(Long brandId, Long productId, LocalDateTime applicationDate) {
        super("No applicable price found for brandId=%d, productId=%d, applicationDate=%s"
                .formatted(brandId, productId, applicationDate));
    }
}
