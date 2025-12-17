package com.mercadolivre.product_api.infrastructure.repository;

import com.mercadolivre.product_api.domain.model.Category;
import com.mercadolivre.product_api.domain.repository.CategoryRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class CategoryRepositoryInMemory implements CategoryRepository {

    private final Map<String, Category> database = new ConcurrentHashMap<>();

    public CategoryRepositoryInMemory() {
        // Dados iniciais para teste
        initializeDefaultCategories();
    }

    private void initializeDefaultCategories() {
        Category eletronicos = Category.builder()
                .id("1")
                .name("Eletrônicos")
                .description("Produtos eletrônicos e gadgets")
                .slug("eletronicos")
                .productCount(0)
                .build();

        Category moda = Category.builder()
                .id("2")
                .name("Moda")
                .description("Roupas, calçados e acessórios")
                .slug("moda")
                .productCount(0)
                .build();

        Category casa = Category.builder()
                .id("3")
                .name("Casa e Decoração")
                .description("Móveis e itens para casa")
                .slug("casa-decoracao")
                .productCount(0)
                .build();

        database.put(eletronicos.getId(), eletronicos);
        database.put(moda.getId(), moda);
        database.put(casa.getId(), casa);
    }

    @Override
    public List<Category> findAll() {
        return new ArrayList<>(database.values());
    }

    @Override
    public Optional<Category> findById(String id) {
        return Optional.ofNullable(database.get(id));
    }

    @Override
    public Optional<Category> findBySlug(String slug) {
        return database.values().stream()
                .filter(c -> c.getSlug() != null && c.getSlug().equals(slug))
                .findFirst();
    }

}
