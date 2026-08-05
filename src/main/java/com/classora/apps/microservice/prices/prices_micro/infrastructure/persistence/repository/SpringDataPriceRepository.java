package com.classora.apps.microservice.prices.prices_micro.infrastructure.persistence.repository;

import com.classora.apps.microservice.prices.prices_micro.infrastructure.persistence.entity.PriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Spring Data repository over the {@code PRICES} table.
 *
 * <p>The derived query selects the rows matching brand, product and date range
 * ({@code startDate <= applicationDate <= endDate}), orders them by descending
 * priority and returns only the first one.
 */
public interface SpringDataPriceRepository extends JpaRepository<PriceEntity, Long> {

    Optional<PriceEntity>
    findFirstByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByPriorityDesc(
            Long brandId, Long productId, LocalDateTime startDate, LocalDateTime endDate);
}
