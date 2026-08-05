package com.classora.apps.microservice.prices.prices_micro.application.usecase;

import com.classora.apps.microservice.prices.prices_micro.domain.model.Price;

import java.time.LocalDateTime;

/**
 * Inbound port that resolves the price applicable to a product of a brand at
 * a given instant.
 */
@FunctionalInterface
public interface GetApplicablePriceUseCase {

    Price getApplicablePrice(LocalDateTime applicationDate, Long productId, Long brandId);
}

