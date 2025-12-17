package com.mercadolivre.product_api.application.service;

import com.mercadolivre.product_api.application.dto.PageResponseDTO;
import com.mercadolivre.product_api.application.dto.ProductImageDTO;

public interface IProductImageService {
    
    PageResponseDTO<ProductImageDTO> getImagesByProductId(String productId, int page, int size);
}
