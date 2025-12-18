package com.mercadolivre.product_api.presentation.controller;

import com.mercadolivre.product_api.application.dto.PageResponseDTO;
import com.mercadolivre.product_api.application.dto.ProductResponseDTO;
import com.mercadolivre.product_api.application.service.IProductService;
import com.mercadolivre.product_api.domain.dto.ApiResponse;
import com.mercadolivre.product_api.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@DisplayName("ProductController Tests")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IProductService productService;

    private ProductResponseDTO productDTO;
    private PageResponseDTO<ProductResponseDTO> pageResponse;

    @BeforeEach
    void setUp() {
        productDTO = ProductResponseDTO.builder()
                .id("1")
                .name("Product 1")
                .description("Description 1")
                .price(new BigDecimal("100.00"))
                .quantity(10)
                .category("electronics")
                .active(true)
                .build();

        List<ProductResponseDTO> content = Arrays.asList(productDTO);
        pageResponse = PageResponseDTO.<ProductResponseDTO>builder()
                .content(content)
                .pageNumber(0)
                .pageSize(20)
                .totalElements(1)
                .totalPages(1)
                .first(true)
                .last(true)
                .empty(false)
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/products should return all products")
    void shouldReturnAllProducts() throws Exception {
        // Given
        when(productService.getAllProducts(anyInt(), anyInt(), any(), any())).thenReturn(pageResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/products")
                        .param("page", "0")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].id").value("1"))
                .andExpect(jsonPath("$.data.content[0].name").value("Product 1"))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/products with filters should return filtered products")
    void shouldReturnFilteredProducts() throws Exception {
        // Given
        when(productService.getAllProducts(anyInt(), anyInt(), eq("Product"), eq("electronics")))
                .thenReturn(pageResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/products")
                        .param("name", "Product")
                        .param("category", "electronics")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/products/{id} should return product by ID")
    void shouldReturnProductById() throws Exception {
        // Given
        when(productService.getProductById("1")).thenReturn(productDTO);

        // When & Then
        mockMvc.perform(get("/api/v1/products/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("1"))
                .andExpect(jsonPath("$.data.name").value("Product 1"));
    }

    @Test
    @DisplayName("GET /api/v1/products/{id} should return 404 when not found")
    void shouldReturn404WhenProductNotFound() throws Exception {
        // Given
        when(productService.getProductById("999"))
                .thenThrow(new ResourceNotFoundException("Product", "id", "999"));

        // When & Then
        mockMvc.perform(get("/api/v1/products/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("GET /api/v1/products/category/{category} should return products by category")
    void shouldReturnProductsByCategory() throws Exception {
        // Given
        when(productService.getProductsByCategory(eq("electronics"), anyInt(), anyInt()))
                .thenReturn(pageResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/products/category/electronics")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].category").value("electronics"));
    }

    @Test
    @DisplayName("GET /api/v1/products/{id}/recommended should return recommended products")
    void shouldReturnRecommendedProducts() throws Exception {
        // Given
        when(productService.getRecommendedProducts(eq("1"), anyInt(), anyInt()))
                .thenReturn(pageResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/products/1/recommended")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should enforce max page size of 50")
    void shouldEnforceMaxPageSize() throws Exception {
        // Given
        when(productService.getAllProducts(anyInt(), eq(50), any(), any())).thenReturn(pageResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/products")
                        .param("size", "100")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/v1/products without parameters should use defaults")
    void shouldUseDefaultParameters() throws Exception {
        // Given
        when(productService.getAllProducts(eq(0), eq(20), eq(null), eq(null))).thenReturn(pageResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/v1/products/category/{category} should enforce max size limit")
    void shouldEnforceMaxSizeForCategory() throws Exception {
        // Given
        when(productService.getProductsByCategory(eq("electronics"), anyInt(), eq(50)))
                .thenReturn(pageResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/products/category/electronics")
                        .param("size", "100")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/v1/products/{id}/recommended should enforce max size limit")
    void shouldEnforceMaxSizeForRecommended() throws Exception {
        // Given
        when(productService.getRecommendedProducts(eq("1"), anyInt(), eq(50)))
                .thenReturn(pageResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/products/1/recommended")
                        .param("size", "100")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
