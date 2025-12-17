package com.mercadolivre.product_api.domain.repository;

import java.util.List;
import java.util.Optional;

import com.mercadolivre.product_api.domain.model.Category;

public interface CategoryRepository {

    List<Category> findAll();

    Optional<Category> findById(String id);

    Optional<Category> findBySlug(String slug);

}
