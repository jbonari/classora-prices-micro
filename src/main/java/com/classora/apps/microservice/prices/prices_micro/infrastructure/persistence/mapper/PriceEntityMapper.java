package com.classora.apps.microservice.prices.prices_micro.infrastructure.persistence.mapper;

import com.classora.apps.microservice.prices.prices_micro.domain.model.Price;
import com.classora.apps.microservice.prices.prices_micro.infrastructure.persistence.entity.PriceEntity;

/**
 * Translates persistence entities into domain models.
 */
public final class PriceEntityMapper {

    private PriceEntityMapper() {
    }

    public static Price toDomain(PriceEntity entity) {
        return new Price(
                entity.getBrandId(),
                entity.getProductId(),
                entity.getPriceList(),
                entity.getPriority(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getPrice(),
                entity.getCurrency());
    }
}
