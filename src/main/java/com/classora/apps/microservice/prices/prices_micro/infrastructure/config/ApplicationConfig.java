package com.classora.apps.microservice.prices.prices_micro.infrastructure.config;

import com.classora.apps.microservice.prices.prices_micro.application.usecase.DefaultGetApplicablePriceUseCase;
import com.classora.apps.microservice.prices.prices_micro.application.usecase.GetApplicablePriceUseCase;
import com.classora.apps.microservice.prices.prices_micro.domain.ports.PriceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Wires application use cases as Spring beans, keeping the use case
 * implementations free of framework annotations.
 */
@Configuration
public class ApplicationConfig {

    @Bean
    GetApplicablePriceUseCase getApplicablePriceUseCase(PriceRepository repository) {
        return new DefaultGetApplicablePriceUseCase(repository);
    }
}

