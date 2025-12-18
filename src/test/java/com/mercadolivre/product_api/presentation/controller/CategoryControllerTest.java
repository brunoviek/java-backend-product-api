package com.mercadolivre.product_api.presentation.controller;

import com.mercadolivre.product_api.application.dto.CategoryDTO;
import com.mercadolivre.product_api.application.dto.PageResponseDTO;
import com.mercadolivre.product_api.application.service.ICategoryService;
import com.mercadolivre.product_api.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ICategoryService categoryService;

    @Test
    void shouldGetAllCategories() throws Exception {
        CategoryDTO category = new CategoryDTO();
        category.setId("1");
        category.setName("Electronics");
        category.setSlug("electronics");
        
        PageResponseDTO<CategoryDTO> page = new PageResponseDTO<>(
            Arrays.asList(category), 0, 10, 1, 1, true, true, false);
        
        when(categoryService.getAllCategories(0, 10)).thenReturn(page);
        
        mockMvc.perform(get("/api/v1/categories")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.pageNumber").value(0))
                .andExpect(jsonPath("$.data.pageSize").value(10));
    }

    @Test
    void shouldGetAllCategoriesWithMaxSizeLimit() throws Exception {
        PageResponseDTO<CategoryDTO> page = new PageResponseDTO<>(
            Arrays.asList(), 0, 50, 0, 0, true, true, true);
        
        when(categoryService.getAllCategories(0, 50)).thenReturn(page);
        
        mockMvc.perform(get("/api/v1/categories")
                .param("page", "0")
                .param("size", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.pageSize").value(50));
    }

    @Test
    void shouldGetCategoryById() throws Exception {
        CategoryDTO category = new CategoryDTO();
        category.setId("1");
        category.setName("Electronics");
        category.setSlug("electronics");
        
        when(categoryService.getCategoryById("1")).thenReturn(category);
        
        mockMvc.perform(get("/api/v1/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("1"))
                .andExpect(jsonPath("$.data.name").exists())
                .andExpect(jsonPath("$.data.slug").exists());
    }

    @Test
    void shouldReturn404WhenCategoryByIdNotFound() throws Exception {
        when(categoryService.getCategoryById("999"))
            .thenThrow(new ResourceNotFoundException("Category not found with id: 999"));
        
        mockMvc.perform(get("/api/v1/categories/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void shouldGetCategoryBySlug() throws Exception {
        CategoryDTO category = new CategoryDTO();
        category.setId("1");
        category.setName("Electronics");
        category.setSlug("electronics");
        
        when(categoryService.getCategoryBySlug("electronics")).thenReturn(category);
        
        mockMvc.perform(get("/api/v1/categories/slug/electronics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.slug").value("electronics"));
    }

    @Test
    void shouldReturn404WhenCategoryBySlugNotFound() throws Exception {
        when(categoryService.getCategoryBySlug("nonexistent"))
            .thenThrow(new ResourceNotFoundException("Category not found with slug: nonexistent"));
        
        mockMvc.perform(get("/api/v1/categories/slug/nonexistent"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void shouldGetAllCategoriesWithDefaultPagination() throws Exception {
        PageResponseDTO<CategoryDTO> page = new PageResponseDTO<>(
            Arrays.asList(), 0, 20, 0, 0, true, true, true);
        
        when(categoryService.getAllCategories(0, 20)).thenReturn(page);
        
        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.pageNumber").value(0))
                .andExpect(jsonPath("$.data.pageSize").value(20));
    }
}
