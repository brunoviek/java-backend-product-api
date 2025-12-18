package com.mercadolivre.product_api.infrastructure.repository;

import com.mercadolivre.product_api.domain.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CategoryRepositoryInMemoryTest {

    private CategoryRepositoryInMemory repository;

    @BeforeEach
    void setUp() {
        repository = new CategoryRepositoryInMemory();
    }

    @Test
    void shouldFindAllCategories() {
        List<Category> categories = repository.findAll();
        assertNotNull(categories);
        assertEquals(3, categories.size());
    }

    @Test
    void shouldFindCategoryById() {
        Optional<Category> category = repository.findById("1");
        assertTrue(category.isPresent());
        assertEquals("eletronicos", category.get().getSlug());
        assertEquals("Eletrônicos", category.get().getName());
    }

    @Test
    void shouldReturnEmptyWhenCategoryNotFoundById() {
        Optional<Category> category = repository.findById("999");
        assertFalse(category.isPresent());
    }

    @Test
    void shouldFindCategoryBySlug() {
        Optional<Category> category = repository.findBySlug("eletronicos");
        assertTrue(category.isPresent());
        assertEquals("1", category.get().getId());
        assertEquals("Eletrônicos", category.get().getName());
    }

    @Test
    void shouldReturnEmptyWhenCategoryNotFoundBySlug() {
        Optional<Category> category = repository.findBySlug("nonexistent");
        assertFalse(category.isPresent());
    }

    @Test
    void shouldGetAllCategorySlugs() {
        List<Category> categories = repository.findAll();
        assertTrue(categories.stream().anyMatch(c -> c.getSlug().equals("eletronicos")));
        assertTrue(categories.stream().anyMatch(c -> c.getSlug().equals("moda")));
        assertTrue(categories.stream().anyMatch(c -> c.getSlug().equals("casa-decoracao")));
    }

    @Test
    void shouldHaveCorrectCategoryDescriptions() {
        Optional<Category> eletronicos = repository.findBySlug("eletronicos");
        assertTrue(eletronicos.isPresent());
        assertEquals("Produtos eletrônicos e gadgets", eletronicos.get().getDescription());

        Optional<Category> moda = repository.findBySlug("moda");
        assertTrue(moda.isPresent());
        assertEquals("Roupas, calçados e acessórios", moda.get().getDescription());
    }

    @Test
    void shouldHaveCorrectProductCounts() {
        List<Category> categories = repository.findAll();
        categories.forEach(category -> {
            assertNotNull(category.getProductCount());
            assertTrue(category.getProductCount() >= 0);
        });
    }

    @Test
    void shouldFindAllCategoriesWithNonNullIds() {
        List<Category> categories = repository.findAll();
        categories.forEach(category -> {
            assertNotNull(category.getId());
            assertNotNull(category.getName());
            assertNotNull(category.getSlug());
        });
    }

    @Test
    void shouldReturnEmptyWhenFindingByNullSlug() {
        Optional<Category> category = repository.findBySlug(null);
        assertFalse(category.isPresent());
    }
}
