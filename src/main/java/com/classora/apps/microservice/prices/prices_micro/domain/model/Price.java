package com.classora.apps.microservice.prices.prices_micro.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Immutable domain representation of an applicable price.
 *
 * <p>This model belongs to the domain layer and is intentionally free of any
 * Spring or JPA dependency.
 */
public record Price(
        Long brandId,
        Long productId,
        Integer priceList,
        Integer priority,
        LocalDateTime startDate,
        LocalDateTime endDate,
        BigDecimal price,
        String currency) {
}
