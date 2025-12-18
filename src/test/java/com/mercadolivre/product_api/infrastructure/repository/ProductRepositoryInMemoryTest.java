package com.mercadolivre.product_api.infrastructure.repository;

import com.mercadolivre.product_api.domain.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ProductRepositoryInMemoryTest {

    private ProductRepositoryInMemory repository;

    @BeforeEach
    void setUp() {
        repository = new ProductRepositoryInMemory();
    }

    @Test
    void shouldSaveProduct() {
        Product product = createProduct("1", "Test Product", "electronics");
        
        Product saved = repository.save(product);
        
        assertNotNull(saved);
        assertEquals("1", saved.getId());
        assertEquals("Test Product", saved.getName());
    }

    @Test
    void shouldUpdateExistingProduct() {
        Product product = createProduct("1", "Original Name", "electronics");
        repository.save(product);
        
        product.setName("Updated Name");
        Product updated = repository.save(product);
        
        assertEquals("Updated Name", updated.getName());
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void shouldFindById() {
        Product product = createProduct("1", "Test Product", "electronics");
        repository.save(product);
        
        Optional<Product> found = repository.findById("1");
        
        assertTrue(found.isPresent());
        assertEquals("Test Product", found.get().getName());
    }

    @Test
    void shouldReturnEmptyWhenIdNotFound() {
        Optional<Product> found = repository.findById("999");
        
        assertFalse(found.isPresent());
    }

    @Test
    void shouldFindByCategory() {
        repository.save(createProduct("1", "Product 1", "electronics"));
        repository.save(createProduct("2", "Product 2", "electronics"));
        repository.save(createProduct("3", "Product 3", "fashion"));
        
        List<Product> electronics = repository.findByCategory("electronics");
        
        assertEquals(2, electronics.size());
        assertTrue(electronics.stream().allMatch(p -> "electronics".equals(p.getCategory())));
    }

    @Test
    void shouldFindAll() {
        repository.save(createProduct("1", "Product 1", "electronics"));
        repository.save(createProduct("2", "Product 2", "fashion"));
        repository.save(createProduct("3", "Product 3", "books"));
        
        List<Product> all = repository.findAll();
        
        assertEquals(3, all.size());
    }

    @Test
    void shouldDeleteProductById() {
        Product product = createProduct("1", "Test Product", "electronics");
        repository.save(product);
        
        repository.deleteById("1");
        
        assertFalse(repository.findById("1").isPresent());
        assertEquals(0, repository.findAll().size());
    }

    @Test
    void shouldCheckIfExistsById() {
        repository.save(createProduct("1", "Test Product", "electronics"));
        
        assertTrue(repository.existsById("1"));
        assertFalse(repository.existsById("999"));
    }

    private Product createProduct(String id, String name, String category) {
        return Product.builder()
                .id(id)
                .name(name)
                .description("Test description")
                .price(BigDecimal.valueOf(99.99))
                .quantity(10)
                .category(category)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
