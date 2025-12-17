package com.mercadolivre.product_api.application.service;

import com.mercadolivre.product_api.application.dto.PageResponseDTO;
import com.mercadolivre.product_api.application.dto.ProductResponseDTO;
import com.mercadolivre.product_api.domain.event.ProductViewedEvent;
import com.mercadolivre.product_api.domain.exception.ResourceNotFoundException;
import com.mercadolivre.product_api.domain.model.Product;
import com.mercadolivre.product_api.domain.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Tests")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ProductService productService;

    private Product product1;
    private Product product2;
    private Product product3;

    @BeforeEach
    void setUp() {
        product1 = new Product();
        product1.setId("1");
        product1.setName("Product 1");
        product1.setDescription("Description 1");
        product1.setPrice(new BigDecimal("100.00"));
        product1.setQuantity(10);
        product1.setCategory("electronics");
        product1.setActive(true);

        product2 = new Product();
        product2.setId("2");
        product2.setName("Product 2");
        product2.setDescription("Description 2");
        product2.setPrice(new BigDecimal("200.00"));
        product2.setQuantity(20);
        product2.setCategory("electronics");
        product2.setActive(true);

        product3 = new Product();
        product3.setId("3");
        product3.setName("Product 3");
        product3.setDescription("Description 3");
        product3.setPrice(new BigDecimal("300.00"));
        product3.setQuantity(30);
        product3.setCategory("fashion");
        product3.setActive(true);
    }

    @Test
    @DisplayName("Should return product by ID and publish event")
    void shouldReturnProductByIdAndPublishEvent() {
        // Given
        when(productRepository.findById("1")).thenReturn(Optional.of(product1));

        // When
        ProductResponseDTO result = productService.getProductById("1");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("1");
        assertThat(result.getName()).isEqualTo("Product 1");
        assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal("100.00"));

        verify(productRepository).findById("1");
        verify(eventPublisher).publishEvent(any(ProductViewedEvent.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when product not found")
    void shouldThrowExceptionWhenProductNotFound() {
        // Given
        when(productRepository.findById("999")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productService.getProductById("999"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product not found with id: 999");

        verify(productRepository).findById("999");
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("Should return paginated products without filters")
    void shouldReturnPaginatedProductsWithoutFilters() {
        // Given
        List<Product> products = Arrays.asList(product1, product2, product3);
        when(productRepository.findAll()).thenReturn(products);

        // When
        PageResponseDTO<ProductResponseDTO> result = productService.getAllProducts(0, 2, null, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.isFirst()).isTrue();
        assertThat(result.isLast()).isFalse();
        assertThat(result.isEmpty()).isFalse();

        verify(productRepository).findAll();
    }

    @Test
    @DisplayName("Should filter products by name")
    void shouldFilterProductsByName() {
        // Given
        List<Product> products = Arrays.asList(product1, product2, product3);
        when(productRepository.findAll()).thenReturn(products);

        // When
        PageResponseDTO<ProductResponseDTO> result = productService.getAllProducts(0, 10, "Product 1", null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Product 1");

        verify(productRepository).findAll();
    }

    @Test
    @DisplayName("Should filter products by category")
    void shouldFilterProductsByCategory() {
        // Given
        List<Product> products = Arrays.asList(product1, product2, product3);
        when(productRepository.findAll()).thenReturn(products);

        // When
        PageResponseDTO<ProductResponseDTO> result = productService.getAllProducts(0, 10, null, "electronics");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getCategory()).isEqualTo("electronics");

        verify(productRepository).findAll();
    }

    @Test
    @DisplayName("Should filter products by name and category")
    void shouldFilterProductsByNameAndCategory() {
        // Given
        List<Product> products = Arrays.asList(product1, product2, product3);
        when(productRepository.findAll()).thenReturn(products);

        // When
        PageResponseDTO<ProductResponseDTO> result = productService.getAllProducts(0, 10, "Product 2", "electronics");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Product 2");
        assertThat(result.getContent().get(0).getCategory()).isEqualTo("electronics");

        verify(productRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty page when no products match filters")
    void shouldReturnEmptyPageWhenNoMatch() {
        // Given
        List<Product> products = Arrays.asList(product1, product2, product3);
        when(productRepository.findAll()).thenReturn(products);

        // When
        PageResponseDTO<ProductResponseDTO> result = productService.getAllProducts(0, 10, "NonExistent", null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.isEmpty()).isTrue();
        assertThat(result.getTotalElements()).isZero();

        verify(productRepository).findAll();
    }

    @Test
    @DisplayName("Should return products by category")
    void shouldReturnProductsByCategory() {
        // Given
        List<Product> products = Arrays.asList(product1, product2);
        when(productRepository.findByCategory("electronics")).thenReturn(products);

        // When
        PageResponseDTO<ProductResponseDTO> result = productService.getProductsByCategory("electronics", 0, 10);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);

        verify(productRepository).findByCategory("electronics");
    }

    @Test
    @DisplayName("Should return recommended products excluding current product")
    void shouldReturnRecommendedProducts() {
        // Given
        when(productRepository.findById("1")).thenReturn(Optional.of(product1));
        when(productRepository.findByCategory("electronics")).thenReturn(Arrays.asList(product1, product2));

        // When
        PageResponseDTO<ProductResponseDTO> result = productService.getRecommendedProducts("1", 0, 10);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo("2");

        verify(productRepository).findById("1");
        verify(productRepository).findByCategory("electronics");
    }

    @Test
    @DisplayName("Should throw exception when getting recommended products for non-existent product")
    void shouldThrowExceptionWhenGettingRecommendedProductsForNonExistentProduct() {
        // Given
        when(productRepository.findById("999")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productService.getRecommendedProducts("999", 0, 10))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(productRepository).findById("999");
    }

    @Test
    @DisplayName("Should handle pagination correctly on second page")
    void shouldHandlePaginationOnSecondPage() {
        // Given
        List<Product> products = Arrays.asList(product1, product2, product3);
        when(productRepository.findAll()).thenReturn(products);

        // When
        PageResponseDTO<ProductResponseDTO> result = productService.getAllProducts(1, 2, null, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getPageNumber()).isEqualTo(1);
        assertThat(result.isFirst()).isFalse();
        assertThat(result.isLast()).isTrue();

        verify(productRepository).findAll();
    }
}
