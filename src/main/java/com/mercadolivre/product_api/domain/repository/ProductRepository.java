package com.mercadolivre.product_api.domain.repository;

import com.mercadolivre.product_api.domain.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(String id);

    List<Product> findAll();

    List<Product> findByCategory(String category);

    void deleteById(String id);

    boolean existsById(String id);

}
