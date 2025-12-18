package com.mercadolivre.product_api.application.service;

import com.mercadolivre.product_api.domain.model.ProductImage;
import com.mercadolivre.product_api.domain.repository.ProductImageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductImageServiceEdgeCasesTest {

    @Mock
    private ProductImageRepository productImageRepository;

    @InjectMocks
    private ProductImageService productImageService;

    @Test
    void shouldHandleEmptyImages() {
        when(productImageRepository.findByProductId("prod1")).thenReturn(Collections.emptyList());
        
        var result = productImageService.getImagesByProductId("prod1", 0, 10);
        
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.getTotalPages()).isEqualTo(0);
        assertThat(result.isEmpty()).isTrue();
        assertThat(result.isFirst()).isTrue();
        assertThat(result.isLast()).isTrue();
        
        verify(productImageRepository).findByProductId("prod1");
    }

    @Test
    void shouldHandleSingleImage() {
        ProductImage image = createImage("img1", "prod1", 1);
        when(productImageRepository.findByProductId("prod1")).thenReturn(Arrays.asList(image));
        
        var result = productImageService.getImagesByProductId("prod1", 0, 10);
        
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.isFirst()).isTrue();
        assertThat(result.isLast()).isTrue();
        
        verify(productImageRepository).findByProductId("prod1");
    }

    @Test
    void shouldSortImagesByDisplayOrderWithNulls() {
        ProductImage image1 = createImage("img1", "prod1", 3);
        ProductImage image2 = createImage("img2", "prod1", 1);
        ProductImage image3 = createImage("img3", "prod1", null);
        ProductImage image4 = createImage("img4", "prod1", 2);
        ProductImage image5 = createImage("img5", "prod1", null);
        
        when(productImageRepository.findByProductId("prod1"))
                .thenReturn(Arrays.asList(image1, image2, image3, image4, image5));
        
        var result = productImageService.getImagesByProductId("prod1", 0, 10);
        
        assertThat(result.getContent()).hasSize(5);
        assertThat(result.getContent().get(0).getDisplayOrder()).isEqualTo(1);
        assertThat(result.getContent().get(1).getDisplayOrder()).isEqualTo(2);
        assertThat(result.getContent().get(2).getDisplayOrder()).isEqualTo(3);
        assertThat(result.getContent().get(3).getDisplayOrder()).isNull();
        assertThat(result.getContent().get(4).getDisplayOrder()).isNull();
        
        verify(productImageRepository).findByProductId("prod1");
    }

    @Test
    void shouldCalculateTotalPagesCorrectly() {
        ProductImage img1 = createImage("img1", "prod1", 1);
        ProductImage img2 = createImage("img2", "prod1", 2);
        ProductImage img3 = createImage("img3", "prod1", 3);
        ProductImage img4 = createImage("img4", "prod1", 4);
        ProductImage img5 = createImage("img5", "prod1", 5);
        
        when(productImageRepository.findByProductId("prod1"))
                .thenReturn(Arrays.asList(img1, img2, img3, img4, img5));
        
        // Page size 2 should give 3 total pages
        var result1 = productImageService.getImagesByProductId("prod1", 0, 2);
        assertThat(result1.getTotalPages()).isEqualTo(3);
        assertThat(result1.getContent()).hasSize(2);
        assertThat(result1.isFirst()).isTrue();
        assertThat(result1.isLast()).isFalse();
        
        // Page size 3 should give 2 total pages
        var result2 = productImageService.getImagesByProductId("prod1", 0, 3);
        assertThat(result2.getTotalPages()).isEqualTo(2);
        assertThat(result2.getContent()).hasSize(3);
        
        verify(productImageRepository, times(2)).findByProductId("prod1");
    }

    @Test
    void shouldHandlePageBeyondLastPage() {
        ProductImage image = createImage("img1", "prod1", 1);
        when(productImageRepository.findByProductId("prod1")).thenReturn(Arrays.asList(image));
        
        var result = productImageService.getImagesByProductId("prod1", 10, 10);
        
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.isEmpty()).isTrue();
        assertThat(result.isFirst()).isFalse();
        
        verify(productImageRepository).findByProductId("prod1");
    }

    @Test
    void shouldHandleMiddlePage() {
        ProductImage img1 = createImage("img1", "prod1", 1);
        ProductImage img2 = createImage("img2", "prod1", 2);
        ProductImage img3 = createImage("img3", "prod1", 3);
        ProductImage img4 = createImage("img4", "prod1", 4);
        ProductImage img5 = createImage("img5", "prod1", 5);
        
        when(productImageRepository.findByProductId("prod1"))
                .thenReturn(Arrays.asList(img1, img2, img3, img4, img5));
        
        var result = productImageService.getImagesByProductId("prod1", 1, 2);
        
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getPageNumber()).isEqualTo(1);
        assertThat(result.isFirst()).isFalse();
        assertThat(result.isLast()).isFalse();
        assertThat(result.getContent().get(0).getId()).isEqualTo("img3");
        assertThat(result.getContent().get(1).getId()).isEqualTo("img4");
        
        verify(productImageRepository).findByProductId("prod1");
    }

    @Test
    void shouldHandleLastPageWithFewerElements() {
        ProductImage img1 = createImage("img1", "prod1", 1);
        ProductImage img2 = createImage("img2", "prod1", 2);
        ProductImage img3 = createImage("img3", "prod1", 3);
        
        when(productImageRepository.findByProductId("prod1"))
                .thenReturn(Arrays.asList(img1, img2, img3));
        
        var result = productImageService.getImagesByProductId("prod1", 1, 2);
        
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getPageNumber()).isEqualTo(1);
        assertThat(result.isFirst()).isFalse();
        assertThat(result.isLast()).isTrue();
        assertThat(result.getContent().get(0).getId()).isEqualTo("img3");
        
        verify(productImageRepository).findByProductId("prod1");
    }

    @Test
    void shouldMapImageToDTOCorrectly() {
        ProductImage image = createImage("img1", "prod1", 1);
        image.setUrl("https://example.com/image.jpg");
        image.setAltText("Product image");
        image.setIsPrimary(true);
        
        when(productImageRepository.findByProductId("prod1")).thenReturn(Arrays.asList(image));
        
        var result = productImageService.getImagesByProductId("prod1", 0, 10);
        var dto = result.getContent().get(0);
        
        assertThat(dto.getId()).isEqualTo("img1");
        assertThat(dto.getProductId()).isEqualTo("prod1");
        assertThat(dto.getUrl()).isEqualTo("https://example.com/image.jpg");
        assertThat(dto.getAltText()).isEqualTo("Product image");
        assertThat(dto.getIsPrimary()).isTrue();
        assertThat(dto.getDisplayOrder()).isEqualTo(1);
        
        verify(productImageRepository).findByProductId("prod1");
    }

    private ProductImage createImage(String id, String productId, Integer displayOrder) {
        ProductImage image = new ProductImage();
        image.setId(id);
        image.setProductId(productId);
        image.setUrl("https://example.com/" + id + ".jpg");
        image.setAltText("Image " + id);
        image.setIsPrimary(false);
        image.setDisplayOrder(displayOrder);
        return image;
    }
}
