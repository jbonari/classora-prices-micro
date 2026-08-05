package com.classora.apps.microservice.prices.prices_micro.infrastructure.rest;

import com.classora.apps.microservice.prices.prices_micro.application.usecase.GetApplicablePriceUseCase;
import com.classora.apps.microservice.prices.prices_micro.domain.model.Price;
import com.classora.apps.microservice.prices.prices_micro.infrastructure.exception.ApiError;
import com.classora.apps.microservice.prices.prices_micro.infrastructure.rest.dto.PriceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * Inbound HTTP adapter that exposes the {@link GetApplicablePriceUseCase}.
 */
@RestController
@RequestMapping("/prices")
@Tag(name = "Prices", description = "Applicable price query")
public class PriceRestAdapter {

    private final GetApplicablePriceUseCase getApplicablePriceUseCase;

    public PriceRestAdapter(GetApplicablePriceUseCase getApplicablePriceUseCase) {
        this.getApplicablePriceUseCase = getApplicablePriceUseCase;
    }

    @GetMapping
    @Operation(
            summary = "Get the applicable price",
            description = "Returns the price applicable to a product of a brand at a given instant. "
                    + "When several rates overlap, the one with the highest priority is returned.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Applicable price found"),
            @ApiResponse(responseCode = "400", description = "Invalid or missing request parameters",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "No applicable price found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public PriceResponse getApplicablePrice(
            @Parameter(description = "Instant of application (ISO-8601, e.g. 2020-06-14T10:00:00)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime applicationDate,
            @Parameter(description = "Product identifier", example = "35455", required = true)
            @RequestParam Long productId,
            @Parameter(description = "Brand identifier (1 = ZARA)", example = "1", required = true)
            @RequestParam Long brandId) {

        Price price = getApplicablePriceUseCase.getApplicablePrice(applicationDate, productId, brandId);
        return PriceResponse.from(price);
    }
}

