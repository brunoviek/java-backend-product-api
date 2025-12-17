package com.mercadolivre.product_api.infrastructure.repository;

import com.mercadolivre.product_api.domain.model.Product;
import com.mercadolivre.product_api.domain.repository.ProductRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class ProductRepositoryInMemory implements ProductRepository {

    private final Map<String, Product> database = new ConcurrentHashMap<>();

    @Override
    public Product save(Product product) {
        database.put(product.getId(), product);
        return product;
    }

    @Override
    public Optional<Product> findById(String id) {
        return Optional.ofNullable(database.get(id));
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(database.values());
    }

    @Override
    public List<Product> findByCategory(String category) {
        return database.values().stream()
                .filter(p -> p.getCategory() != null && p.getCategory().equals(category))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        database.remove(id);
    }

    @Override
    public boolean existsById(String id) {
        return database.containsKey(id);
    }

}
