package com.mercadolivre.product_api.presentation.controller;

import com.mercadolivre.product_api.application.dto.PageResponseDTO;
import com.mercadolivre.product_api.application.dto.ProductImageDTO;
import com.mercadolivre.product_api.application.service.IProductImageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ProductImageController.class)
class ProductImageControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private IProductImageService productImageService;

    @Test
    void shouldGetImagesByProductId() throws Exception {
        ProductImageDTO image = new ProductImageDTO();
        image.setId("img1");
        image.setProductId("prod1");
        image.setUrl("http://example.com/image1.jpg");
        image.setIsPrimary(true);
        
        PageResponseDTO<ProductImageDTO> page = new PageResponseDTO<>(
            Arrays.asList(image), 0, 10, 1, 1, true, true, false);
        
        when(productImageService.getImagesByProductId("prod1", 0, 10)).thenReturn(page);
        
        mockMvc.perform(get("/api/v1/images/product/prod1")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.pageNumber").value(0))
                .andExpect(jsonPath("$.data.pageSize").value(10));
    }

    @Test
    void shouldGetImagesWithDefaultPagination() throws Exception {
        PageResponseDTO<ProductImageDTO> page = new PageResponseDTO<>(
            Arrays.asList(), 0, 20, 0, 0, true, true, true);
        
        when(productImageService.getImagesByProductId("prod1", 0, 20)).thenReturn(page);
        
        mockMvc.perform(get("/api/v1/images/product/prod1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.pageNumber").value(0))
                .andExpect(jsonPath("$.data.pageSize").value(20));
    }

    @Test
    void shouldGetImagesWithMaxSizeLimit() throws Exception {
        PageResponseDTO<ProductImageDTO> page = new PageResponseDTO<>(
            Arrays.asList(), 0, 50, 0, 0, true, true, true);
        
        when(productImageService.getImagesByProductId("prod1", 0, 50)).thenReturn(page);
        
        mockMvc.perform(get("/api/v1/images/product/prod1")
                .param("page", "0")
                .param("size", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.pageSize").value(50));
    }

    @Test
    void shouldReturnEmptyListForProductWithNoImages() throws Exception {
        PageResponseDTO<ProductImageDTO> page = new PageResponseDTO<>(
            Arrays.asList(), 0, 20, 0, 0, true, true, true);
        
        when(productImageService.getImagesByProductId("nonexistent", 0, 20)).thenReturn(page);
        
        mockMvc.perform(get("/api/v1/images/product/nonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.empty").value(true));
    }
}
