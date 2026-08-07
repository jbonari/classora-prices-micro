package com.classora.apps.microservice.prices.prices_micro.infrastructure.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.closeTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PriceControllerIntegrationTest {

    private static final long PRODUCT_ID = 35455L;
    private static final long BRAND_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Test 1: request at 2020-06-14 10:00 -> rate 1, 35.50 EUR")
    void requestAt10OnDay14ReturnsBaseRate() throws Exception {
        assertPrice("2020-06-14T10:00:00", 1, 35.50);
    }

    @Test
    @DisplayName("Test 2: request at 2020-06-14 16:00 -> rate 2, 25.45 EUR")
    void requestAt16OnDay14ReturnsRate2() throws Exception {
        assertPrice("2020-06-14T16:00:00", 2, 25.45);
    }

    @Test
    @DisplayName("Test 3: request at 2020-06-14 21:00 -> rate 1, 35.50 EUR")
    void requestAt21OnDay14ReturnsBaseRate() throws Exception {
        assertPrice("2020-06-14T21:00:00", 1, 35.50);
    }

    @Test
    @DisplayName("Test 4: request at 2020-06-15 10:00 -> rate 3, 30.50 EUR")
    void requestAt10OnDay15ReturnsRate3() throws Exception {
        assertPrice("2020-06-15T10:00:00", 3, 30.50);
    }

    @Test
    @DisplayName("Test 5: request at 2020-06-16 21:00 -> rate 4, 38.95 EUR")
    void requestAt21OnDay16ReturnsRate4() throws Exception {
        assertPrice("2020-06-16T21:00:00", 4, 38.95);
    }

    @Test
    @DisplayName("Error case: no applicable price -> HTTP 404")
    void requestWithNoApplicablePriceReturnsNotFound() throws Exception {
        mockMvc.perform(get("/prices")
                        .param("applicationDate", "2019-01-01T00:00:00")
                        .param("productId", String.valueOf(PRODUCT_ID))
                        .param("brandId", String.valueOf(BRAND_ID)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("Error case: malformed applicationDate -> HTTP 400")
    void requestWithMalformedDateReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/prices")
                        .param("applicationDate", "2020/06/14")
                        .param("productId", String.valueOf(PRODUCT_ID))
                        .param("brandId", String.valueOf(BRAND_ID)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("Error case: missing productId -> HTTP 400")
    void requestWithoutProductIdReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/prices")
                        .param("applicationDate", "2020-06-14T10:00:00")
                        .param("brandId", String.valueOf(BRAND_ID)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("Error case: unknown product -> HTTP 404")
    void requestWithUnknownProductReturnsNotFound() throws Exception {
        mockMvc.perform(get("/prices")
                        .param("applicationDate", "2020-06-14T10:00:00")
                        .param("productId", "99999")
                        .param("brandId", String.valueOf(BRAND_ID)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    private void assertPrice(String applicationDate, int expectedPriceList, double expectedPrice) throws Exception {
        mockMvc.perform(get("/prices")
                        .param("applicationDate", applicationDate)
                        .param("productId", String.valueOf(PRODUCT_ID))
                        .param("brandId", String.valueOf(BRAND_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(PRODUCT_ID))
                .andExpect(jsonPath("$.brandId").value(BRAND_ID))
                .andExpect(jsonPath("$.priceList").value(expectedPriceList))
                .andExpect(jsonPath("$.price").value(closeTo(expectedPrice, 0.0001)));
    }
}
