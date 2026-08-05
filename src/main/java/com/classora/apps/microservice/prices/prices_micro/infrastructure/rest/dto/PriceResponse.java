package com.classora.apps.microservice.prices.prices_micro.infrastructure.rest.dto;

import com.classora.apps.microservice.prices.prices_micro.domain.model.Price;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * API representation of an applicable price.
 */
public record PriceResponse(
        Long productId,
        Long brandId,
        Integer priceList,
        LocalDateTime startDate,
        LocalDateTime endDate,
        BigDecimal price,
        String currency) {

    public static PriceResponse from(Price price) {
        return new PriceResponse(
                price.productId(),
                price.brandId(),
                price.priceList(),
                price.startDate(),
                price.endDate(),
                price.price(),
                price.currency());
    }
}

