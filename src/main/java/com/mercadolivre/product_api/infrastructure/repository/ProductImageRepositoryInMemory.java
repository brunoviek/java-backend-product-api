package com.mercadolivre.product_api.infrastructure.repository;

import com.mercadolivre.product_api.domain.model.ProductImage;
import com.mercadolivre.product_api.domain.repository.ProductImageRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class ProductImageRepositoryInMemory implements ProductImageRepository {

    private final Map<String, ProductImage> database = new ConcurrentHashMap<>();

    @Override
    public List<ProductImage> findByProductId(String productId) {
        return database.values().stream()
                .filter(img -> img.getProductId() != null && img.getProductId().equals(productId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductImage> findAll() {
        return new ArrayList<>(database.values());
    }
    
    @Override
    public ProductImage save(ProductImage image) {
        database.put(image.getId(), image);
        return image;
    }

}
