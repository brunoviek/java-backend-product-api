package com.mercadolivre.product_api.infrastructure.repository;

import com.mercadolivre.product_api.domain.model.ProductImage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductImageRepositoryInMemoryTest {

    private ProductImageRepositoryInMemory repository;

    @BeforeEach
    void setUp() {
        repository = new ProductImageRepositoryInMemory();
    }

    @Test
    void shouldSaveProductImage() {
        ProductImage image = createImage("img1", "prod1", "http://example.com/image1.jpg", true, 1);
        
        ProductImage saved = repository.save(image);
        
        assertNotNull(saved);
        assertEquals("img1", saved.getId());
        assertEquals("prod1", saved.getProductId());
        assertTrue(saved.getIsPrimary());
    }

    @Test
    void shouldUpdateExistingImage() {
        ProductImage image = createImage("img1", "prod1", "http://example.com/image1.jpg", true, 1);
        repository.save(image);
        
        image.setIsPrimary(false);
        ProductImage updated = repository.save(image);
        
        assertFalse(updated.getIsPrimary());
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void shouldFindByProductId() {
        repository.save(createImage("img1", "prod1", "http://example.com/1.jpg", true, 1));
        repository.save(createImage("img2", "prod1", "http://example.com/2.jpg", false, 2));
        repository.save(createImage("img3", "prod2", "http://example.com/3.jpg", true, 1));
        
        List<ProductImage> images = repository.findByProductId("prod1");
        
        assertEquals(2, images.size());
        assertTrue(images.stream().allMatch(img -> "prod1".equals(img.getProductId())));
    }

    @Test
    void shouldReturnEmptyListWhenProductHasNoImages() {
        List<ProductImage> images = repository.findByProductId("nonexistent");
        
        assertTrue(images.isEmpty());
    }

    @Test
    void shouldFindAll() {
        repository.save(createImage("img1", "prod1", "http://example.com/1.jpg", true, 1));
        repository.save(createImage("img2", "prod1", "http://example.com/2.jpg", false, 2));
        repository.save(createImage("img3", "prod2", "http://example.com/3.jpg", true, 1));
        
        List<ProductImage> all = repository.findAll();
        
        assertEquals(3, all.size());
    }

    @Test
    void shouldHandleNullProductIdInFindByProductId() {
        repository.save(createImage("img1", null, "http://example.com/1.jpg", true, 1));
        repository.save(createImage("img2", "prod1", "http://example.com/2.jpg", false, 2));
        
        List<ProductImage> images = repository.findByProductId("prod1");
        
        assertEquals(1, images.size());
        assertEquals("img2", images.get(0).getId());
    }

    private ProductImage createImage(String id, String productId, String url, Boolean isPrimary, Integer displayOrder) {
        return ProductImage.builder()
                .id(id)
                .productId(productId)
                .url(url)
                .altText("Test image")
                .isPrimary(isPrimary)
                .displayOrder(displayOrder)
                .build();
    }
}
