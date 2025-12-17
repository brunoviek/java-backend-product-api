package com.mercadolivre.product_api.presentation.controller;

import com.mercadolivre.product_api.infrastructure.event.ProductEventListener;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MetricsController.class)
@DisplayName("MetricsController Tests")
class MetricsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductEventListener eventListener;

    @Test
    @DisplayName("GET /api/v1/metrics/products/{productId}/views should return product views")
    void shouldReturnProductViews() throws Exception {
        // Given
        when(eventListener.getProductViewCount("prod1")).thenReturn(10);

        // When & Then
        mockMvc.perform(get("/api/v1/metrics/products/prod1/views")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.productId").value("prod1"))
                .andExpect(jsonPath("$.data.viewCount").value(10))
                .andExpect(jsonPath("$.data.note").exists())
                .andExpect(jsonPath("$.message").value("Product view metrics retrieved successfully"));
    }

    @Test
    @DisplayName("GET /api/v1/metrics/products/{productId}/views should return zero for new product")
    void shouldReturnZeroViewsForNewProduct() throws Exception {
        // Given
        when(eventListener.getProductViewCount("prod999")).thenReturn(0);

        // When & Then
        mockMvc.perform(get("/api/v1/metrics/products/prod999/views")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.viewCount").value(0));
    }

    @Test
    @DisplayName("GET /api/v1/metrics/categories/{category}/views should return category views")
    void shouldReturnCategoryViews() throws Exception {
        // Given
        when(eventListener.getCategoryViewCount("electronics")).thenReturn(25);

        // When & Then
        mockMvc.perform(get("/api/v1/metrics/categories/electronics/views")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.category").value("electronics"))
                .andExpect(jsonPath("$.data.viewCount").value(25))
                .andExpect(jsonPath("$.message").value("Category view metrics retrieved successfully"));
    }

    @Test
    @DisplayName("GET /api/v1/metrics/categories/{category}/views should return zero for new category")
    void shouldReturnZeroViewsForNewCategory() throws Exception {
        // Given
        when(eventListener.getCategoryViewCount("newcategory")).thenReturn(0);

        // When & Then
        mockMvc.perform(get("/api/v1/metrics/categories/newcategory/views")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.viewCount").value(0));
    }
}
