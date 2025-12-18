package com.mercadolivre.product_api.domain.repository;

import java.util.List;

import com.mercadolivre.product_api.domain.model.ProductImage;

public interface ProductImageRepository {

    List<ProductImage> findByProductId(String productId);

    List<ProductImage> findAll();
    
    ProductImage save(ProductImage image);

}
