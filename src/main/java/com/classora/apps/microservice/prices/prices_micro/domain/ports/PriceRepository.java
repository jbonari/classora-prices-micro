package com.classora.apps.microservice.prices.prices_micro.domain.ports;

import com.classora.apps.microservice.prices.prices_micro.domain.model.Price;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Outbound port for retrieving prices.
 *
 * <p>The contract guarantees that, when several prices overlap for the same
 * brand, product and instant, the one with the highest {@code priority} is
 * returned.
 */
public interface PriceRepository {

    Optional<Price> findApplicablePrice(LocalDateTime applicationDate, Long productId, Long brandId);
}
