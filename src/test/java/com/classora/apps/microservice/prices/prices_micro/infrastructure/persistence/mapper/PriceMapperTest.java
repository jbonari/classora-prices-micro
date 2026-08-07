package com.classora.apps.microservice.prices.prices_micro.infrastructure.persistence.mapper;

import com.classora.apps.microservice.prices.prices_micro.domain.model.Price;
import com.classora.apps.microservice.prices.prices_micro.infrastructure.persistence.entity.PriceEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PriceMapperTest {

    private static final Long BRAND_ID = 1L;
    private static final Long PRODUCT_ID = 35455L;
    private static final Integer PRICE_LIST = 2;
    private static final Integer PRIORITY = 1;
    private static final LocalDateTime START_DATE = LocalDateTime.parse("2020-06-14T15:00:00");
    private static final LocalDateTime END_DATE = LocalDateTime.parse("2020-06-14T18:30:00");
    private static final BigDecimal PRICE = new BigDecimal("25.45");
    private static final String CURRENCY = "EUR";

    @Test
    @DisplayName("Maps entity to domain preserving every field")
    void mapsEntityToDomain() {
        PriceEntity entity = sampleEntity();

        Price domain = PriceEntityMapper.toDomain(entity);

        assertThat(domain.brandId()).isEqualTo(BRAND_ID);
        assertThat(domain.productId()).isEqualTo(PRODUCT_ID);
        assertThat(domain.priceList()).isEqualTo(PRICE_LIST);
        assertThat(domain.priority()).isEqualTo(PRIORITY);
        assertThat(domain.startDate()).isEqualTo(START_DATE);
        assertThat(domain.endDate()).isEqualTo(END_DATE);
        assertThat(domain.price()).isEqualByComparingTo(PRICE);
        assertThat(domain.currency()).isEqualTo(CURRENCY);
    }

    /**
     * {@link PriceEntity} is only ever instantiated by Hibernate through its
     * protected no-arg constructor, so the fields are populated reflectively to
     * keep the persistence entity free of test-only constructors.
     */
    private static PriceEntity sampleEntity() {
        PriceEntity entity = BeanUtils.instantiateClass(PriceEntity.class);
        Map.of(
                "brandId", BRAND_ID,
                "productId", PRODUCT_ID,
                "priceList", PRICE_LIST,
                "priority", PRIORITY,
                "startDate", START_DATE,
                "endDate", END_DATE,
                "price", PRICE,
                "currency", CURRENCY
        ).forEach((field, value) -> ReflectionTestUtils.setField(entity, field, value));
        return entity;
    }
}
