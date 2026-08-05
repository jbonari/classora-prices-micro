package com.classora.apps.microservice.prices.prices_micro.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI pricesOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("Prices API")
                .version("1.0.0")
                .description("Service that resolves the applicable price for a product of a brand "
                        + "at a given instant, selecting the rate with the highest priority."));
    }
}
