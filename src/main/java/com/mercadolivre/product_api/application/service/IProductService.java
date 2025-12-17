package com.mercadolivre.product_api.application.service;

import com.mercadolivre.product_api.application.dto.PageResponseDTO;
import com.mercadolivre.product_api.application.dto.ProductResponseDTO;

public interface IProductService {
    
    ProductResponseDTO getProductById(String id);
    
    PageResponseDTO<ProductResponseDTO> getAllProducts(int page, int size, String name, String category);
    
    PageResponseDTO<ProductResponseDTO> getProductsByCategory(String category, int page, int size);
    
    PageResponseDTO<ProductResponseDTO> getRecommendedProducts(String productId, int page, int size);
}
