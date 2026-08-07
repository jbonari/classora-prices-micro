package com.classora.apps.microservice.prices.prices_micro.infrastructure.rest.dto;

import com.classora.apps.microservice.prices.prices_micro.domain.model.Price;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PriceResponseTest {

    private static final Long BRAND_ID = 1L;
    private static final Long PRODUCT_ID = 35455L;
    private static final Integer PRICE_LIST = 2;
    private static final Integer PRIORITY = 1;
    private static final LocalDateTime START_DATE = LocalDateTime.parse("2020-06-14T15:00:00");
    private static final LocalDateTime END_DATE = LocalDateTime.parse("2020-06-14T18:30:00");
    private static final BigDecimal PRICE = new BigDecimal("25.45");
    private static final String CURRENCY = "EUR";

    @Test
    @DisplayName("from() copies every field exposed by the REST response")
    void mapsAllFieldsFromDomainModel() {
        Price price = samplePrice();

        PriceResponse response = PriceResponse.from(price);

        assertThat(response.productId()).isEqualTo(PRODUCT_ID);
        assertThat(response.brandId()).isEqualTo(BRAND_ID);
        assertThat(response.priceList()).isEqualTo(PRICE_LIST);
        assertThat(response.startDate()).isEqualTo(START_DATE);
        assertThat(response.endDate()).isEqualTo(END_DATE);
        assertThat(response.price()).isEqualByComparingTo(PRICE);
        assertThat(response.currency()).isEqualTo(CURRENCY);
    }

    @Test
    @DisplayName("from() keeps the currency ISO code")
    void keepsCurrencyIsoCode() {
        Price price = samplePrice();

        PriceResponse response = PriceResponse.from(price);

        assertThat(response.currency()).isEqualTo("EUR");
    }

    @Test
    @DisplayName("from() preserves the BigDecimal value regardless of scale")
    void preservesBigDecimalValue() {
        Price price = samplePrice();

        PriceResponse response = PriceResponse.from(price);

        assertThat(response.price()).isEqualByComparingTo(PRICE);
    }

    private static Price samplePrice() {
        return new Price(
                BRAND_ID, PRODUCT_ID, PRICE_LIST, PRIORITY, START_DATE, END_DATE, PRICE, CURRENCY);
    }
}
