package com.mercadolivre.product_api.infrastructure.initializer;

import com.mercadolivre.product_api.domain.repository.ProductRepository;
import com.mercadolivre.product_api.infrastructure.repository.ProductImageRepositoryInMemory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DataInitializerTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductImageRepositoryInMemory productImageRepository;

    private DataInitializer dataInitializer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        dataInitializer = new DataInitializer(productRepository, productImageRepository);
    }

    @Test
    void shouldInitializeDataOnStartup() throws Exception {
        dataInitializer.run();

        // Verify that products were saved (90 products)
        verify(productRepository, times(90)).save(any());
        
        // Verify that images were saved (90 products × 3 images = 270 images)
        verify(productImageRepository, times(270)).save(any());
    }

    @Test
    void shouldCreateProductsWithCorrectData() throws Exception {
        dataInitializer.run();

        // Verify at least one save was called
        verify(productRepository, atLeastOnce()).save(argThat(product ->
            product != null &&
            product.getId() != null &&
            product.getName() != null &&
            product.getPrice() != null &&
            product.getQuantity() != null &&
            product.getCategory() != null &&
            product.getActive() != null &&
            product.getActive() &&
            product.getCreatedAt() != null &&
            product.getUpdatedAt() != null
        ));
    }

    @Test
    void shouldCreateImagesWithCorrectData() throws Exception {
        dataInitializer.run();

        // Verify images were created with correct attributes
        verify(productImageRepository, atLeastOnce()).save(argThat(image ->
            image != null &&
            image.getId() != null &&
            image.getProductId() != null &&
            image.getUrl() != null &&
            image.getAltText() != null &&
            image.getIsPrimary() != null &&
            image.getDisplayOrder() != null
        ));
    }

    @Test
    void shouldCreatePrimaryAndSecondaryImages() throws Exception {
        dataInitializer.run();

        // Verify primary images (1 per product = 90)
        verify(productImageRepository, times(90)).save(argThat(image ->
            image != null && image.getIsPrimary() != null && image.getIsPrimary()
        ));

        // Verify secondary images (2 per product = 180)
        verify(productImageRepository, times(180)).save(argThat(image ->
            image != null && image.getIsPrimary() != null && !image.getIsPrimary()
        ));
    }
}
