package com.classora.apps.microservice.prices.prices_micro.application.usecase;

import com.classora.apps.microservice.prices.prices_micro.domain.exception.PriceNotFoundException;
import com.classora.apps.microservice.prices.prices_micro.domain.model.Price;
import com.classora.apps.microservice.prices.prices_micro.domain.ports.PriceRepository;

import java.time.LocalDateTime;

/**
 * Default implementation of {@link GetApplicablePriceUseCase}.
 *
 * <p>When several rates overlap, the one with the highest {@code priority} is
 * selected. If no rate applies a {@link PriceNotFoundException} is raised.
 *
 * <p>This class is free of Spring annotations; its wiring as a bean is the
 * responsibility of the infrastructure layer (see {@code ApplicationConfig}).
 */
public class DefaultGetApplicablePriceUseCase implements GetApplicablePriceUseCase {

    private final PriceRepository priceRepository;

    public DefaultGetApplicablePriceUseCase(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    @Override
    public Price getApplicablePrice(LocalDateTime applicationDate, Long productId, Long brandId) {
        return priceRepository.findApplicablePrice(applicationDate, productId, brandId)
                .orElseThrow(() -> new PriceNotFoundException(brandId, productId, applicationDate));
    }
}

