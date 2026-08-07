package com.classora.apps.microservice.prices.prices_micro.application.usecase;

import com.classora.apps.microservice.prices.prices_micro.domain.exception.PriceNotFoundException;
import com.classora.apps.microservice.prices.prices_micro.domain.model.Price;
import com.classora.apps.microservice.prices.prices_micro.domain.ports.PriceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultGetApplicablePriceUseCaseTest {

    private static final LocalDateTime APPLICATION_DATE = LocalDateTime.parse("2020-06-14T16:00:00");
    private static final Long PRODUCT_ID = 35455L;
    private static final Long BRAND_ID = 1L;

    @Mock
    private PriceRepository priceRepository;

    @InjectMocks
    private DefaultGetApplicablePriceUseCase useCase;

    @Test
    @DisplayName("Returns exactly the price supplied by the repository, without transformation")
    void returnsPriceFromRepository() {
        Price expected = samplePrice();
        when(priceRepository.findApplicablePrice(APPLICATION_DATE, PRODUCT_ID, BRAND_ID))
                .thenReturn(Optional.of(expected));

        Price result = useCase.getApplicablePrice(APPLICATION_DATE, PRODUCT_ID, BRAND_ID);

        assertThat(result).isSameAs(expected);
        verify(priceRepository).findApplicablePrice(APPLICATION_DATE, PRODUCT_ID, BRAND_ID);
        verifyNoMoreInteractions(priceRepository);
    }

    @Test
    @DisplayName("Throws PriceNotFoundException when the repository finds no applicable price")
    void throwsWhenNoPriceApplies() {
        when(priceRepository.findApplicablePrice(APPLICATION_DATE, PRODUCT_ID, BRAND_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.getApplicablePrice(APPLICATION_DATE, PRODUCT_ID, BRAND_ID))
                .isInstanceOf(PriceNotFoundException.class);

        verify(priceRepository).findApplicablePrice(APPLICATION_DATE, PRODUCT_ID, BRAND_ID);
        verifyNoMoreInteractions(priceRepository);
    }

    private static Price samplePrice() {
        return new Price(
                BRAND_ID,
                PRODUCT_ID,
                2,
                1,
                LocalDateTime.parse("2020-06-14T15:00:00"),
                LocalDateTime.parse("2020-06-14T18:30:00"),
                new BigDecimal("25.45"),
                "EUR");
    }
}
