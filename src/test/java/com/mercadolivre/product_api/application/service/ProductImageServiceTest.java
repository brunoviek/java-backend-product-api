package com.mercadolivre.product_api.application.service;

import com.mercadolivre.product_api.application.dto.PageResponseDTO;
import com.mercadolivre.product_api.application.dto.ProductImageDTO;
import com.mercadolivre.product_api.domain.model.ProductImage;
import com.mercadolivre.product_api.domain.repository.ProductImageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductImageService Tests")
class ProductImageServiceTest {

    @Mock
    private ProductImageRepository productImageRepository;

    @InjectMocks
    private ProductImageService productImageService;

    private ProductImage image1;
    private ProductImage image2;
    private ProductImage image3;

    @BeforeEach
    void setUp() {
        image1 = new ProductImage();
        image1.setId("img1");
        image1.setProductId("prod1");
        image1.setUrl("http://example.com/img1.jpg");
        image1.setAltText("Image 1");
        image1.setIsPrimary(true);
        image1.setDisplayOrder(1);

        image2 = new ProductImage();
        image2.setId("img2");
        image2.setProductId("prod1");
        image2.setUrl("http://example.com/img2.jpg");
        image2.setAltText("Image 2");
        image2.setIsPrimary(false);
        image2.setDisplayOrder(2);

        image3 = new ProductImage();
        image3.setId("img3");
        image3.setProductId("prod1");
        image3.setUrl("http://example.com/img3.jpg");
        image3.setAltText("Image 3");
        image3.setIsPrimary(false);
        image3.setDisplayOrder(3);
    }

    @Test
    @DisplayName("Should return images by product ID with pagination")
    void shouldReturnImagesByProductId() {
        // Given
        List<ProductImage> images = Arrays.asList(image1, image2, image3);
        when(productImageRepository.findByProductId("prod1")).thenReturn(images);

        // When
        PageResponseDTO<ProductImageDTO> result = productImageService.getImagesByProductId("prod1", 0, 10);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getContent().get(0).getDisplayOrder()).isEqualTo(1);
        assertThat(result.getContent().get(1).getDisplayOrder()).isEqualTo(2);
        assertThat(result.getContent().get(2).getDisplayOrder()).isEqualTo(3);

        verify(productImageRepository).findByProductId("prod1");
    }

    @Test
    @DisplayName("Should sort images by display order")
    void shouldSortImagesByDisplayOrder() {
        // Given
        ProductImage unsortedImage = new ProductImage();
        unsortedImage.setId("img4");
        unsortedImage.setProductId("prod1");
        unsortedImage.setDisplayOrder(null); // Null order should be last

        List<ProductImage> images = Arrays.asList(image3, image1, unsortedImage, image2);
        when(productImageRepository.findByProductId("prod1")).thenReturn(images);

        // When
        PageResponseDTO<ProductImageDTO> result = productImageService.getImagesByProductId("prod1", 0, 10);

        // Then
        assertThat(result.getContent()).hasSize(4);
        assertThat(result.getContent().get(0).getDisplayOrder()).isEqualTo(1);
        assertThat(result.getContent().get(1).getDisplayOrder()).isEqualTo(2);
        assertThat(result.getContent().get(2).getDisplayOrder()).isEqualTo(3);
        assertThat(result.getContent().get(3).getDisplayOrder()).isNull();

        verify(productImageRepository).findByProductId("prod1");
    }

    @Test
    @DisplayName("Should handle pagination correctly")
    void shouldHandlePaginationCorrectly() {
        // Given
        List<ProductImage> images = Arrays.asList(image1, image2, image3);
        when(productImageRepository.findByProductId("prod1")).thenReturn(images);

        // When
        PageResponseDTO<ProductImageDTO> result = productImageService.getImagesByProductId("prod1", 0, 2);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.isFirst()).isTrue();
        assertThat(result.isLast()).isFalse();

        verify(productImageRepository).findByProductId("prod1");
    }

    @Test
    @DisplayName("Should return empty list when product has no images")
    void shouldReturnEmptyListWhenNoImages() {
        // Given
        when(productImageRepository.findByProductId("prod2")).thenReturn(List.of());

        // When
        PageResponseDTO<ProductImageDTO> result = productImageService.getImagesByProductId("prod2", 0, 10);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.isEmpty()).isTrue();
        assertThat(result.getTotalElements()).isZero();

        verify(productImageRepository).findByProductId("prod2");
    }

    @Test
    @DisplayName("Should return second page correctly")
    void shouldReturnSecondPageCorrectly() {
        // Given
        List<ProductImage> images = Arrays.asList(image1, image2, image3);
        when(productImageRepository.findByProductId("prod1")).thenReturn(images);

        // When
        PageResponseDTO<ProductImageDTO> result = productImageService.getImagesByProductId("prod1", 1, 2);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getPageNumber()).isEqualTo(1);
        assertThat(result.isFirst()).isFalse();
        assertThat(result.isLast()).isTrue();

        verify(productImageRepository).findByProductId("prod1");
    }

    @Test
    @DisplayName("Should return empty page when page number exceeds available pages")
    void shouldReturnEmptyPageWhenPageNumberExceedsAvailablePages() {
        // Given
        List<ProductImage> images = Arrays.asList(image1);
        when(productImageRepository.findByProductId("prod1")).thenReturn(images);

        // When
        PageResponseDTO<ProductImageDTO> result = productImageService.getImagesByProductId("prod1", 5, 10);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.isEmpty()).isTrue();
        assertThat(result.getTotalElements()).isEqualTo(1);

        verify(productImageRepository).findByProductId("prod1");
    }

    @Test
    @DisplayName("Should handle images with same display order")
    void shouldHandleImagesWithSameDisplayOrder() {
        // Given
        ProductImage image4 = new ProductImage();
        image4.setId("img4");
        image4.setProductId("prod1");
        image4.setDisplayOrder(1); // Same as image1

        List<ProductImage> images = Arrays.asList(image1, image4);
        when(productImageRepository.findByProductId("prod1")).thenReturn(images);

        // When
        PageResponseDTO<ProductImageDTO> result = productImageService.getImagesByProductId("prod1", 0, 10);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getDisplayOrder()).isEqualTo(1);
        assertThat(result.getContent().get(1).getDisplayOrder()).isEqualTo(1);

        verify(productImageRepository).findByProductId("prod1");
    }

    @Test
    @DisplayName("Should handle all images with null display order")
    void shouldHandleAllImagesWithNullDisplayOrder() {
        // Given
        ProductImage imageWithNullOrder1 = new ProductImage();
        imageWithNullOrder1.setId("img4");
        imageWithNullOrder1.setProductId("prod1");
        imageWithNullOrder1.setDisplayOrder(null);

        ProductImage imageWithNullOrder2 = new ProductImage();
        imageWithNullOrder2.setId("img5");
        imageWithNullOrder2.setProductId("prod1");
        imageWithNullOrder2.setDisplayOrder(null);

        List<ProductImage> images = Arrays.asList(imageWithNullOrder1, imageWithNullOrder2);
        when(productImageRepository.findByProductId("prod1")).thenReturn(images);

        // When
        PageResponseDTO<ProductImageDTO> result = productImageService.getImagesByProductId("prod1", 0, 10);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getDisplayOrder()).isNull();
        assertThat(result.getContent().get(1).getDisplayOrder()).isNull();

        verify(productImageRepository).findByProductId("prod1");
    }
}
