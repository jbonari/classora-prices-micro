package com.classora.apps.microservice.prices.prices_micro.infrastructure.persistence.repository;

import com.classora.apps.microservice.prices.prices_micro.domain.model.Price;
import com.classora.apps.microservice.prices.prices_micro.domain.ports.PriceRepository;
import com.classora.apps.microservice.prices.prices_micro.infrastructure.persistence.mapper.PriceEntityMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Adapter that implements the domain {@link PriceRepository} port on top of
 * Spring Data JPA.
 */
@Repository
public class JpaPriceRepository implements PriceRepository {

    private final SpringDataPriceRepository repository;

    public JpaPriceRepository(SpringDataPriceRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Price> findApplicablePrice(LocalDateTime applicationDate, Long productId, Long brandId) {
        return repository
                .findFirstByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByPriorityDesc(
                        brandId, productId, applicationDate, applicationDate)
                .map(PriceEntityMapper::toDomain);
    }
}
