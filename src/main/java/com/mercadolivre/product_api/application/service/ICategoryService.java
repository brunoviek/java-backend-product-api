package com.mercadolivre.product_api.application.service;

import com.mercadolivre.product_api.application.dto.CategoryDTO;
import com.mercadolivre.product_api.application.dto.PageResponseDTO;

public interface ICategoryService {
    
    PageResponseDTO<CategoryDTO> getAllCategories(int page, int size);
    
    CategoryDTO getCategoryById(String id);
    
    CategoryDTO getCategoryBySlug(String slug);
}
