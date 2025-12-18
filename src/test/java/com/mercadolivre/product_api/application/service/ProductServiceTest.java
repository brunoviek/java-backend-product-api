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

    @Test
    @DisplayName("Should filter by name only")
    void shouldFilterByNameOnly() {
        // Given
        List<Product> products = Arrays.asList(product1);
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
    @DisplayName("Should filter by category only")
    void shouldFilterByCategoryOnly() {
        // Given
        List<Product> products = Arrays.asList(product1, product2, product3);
        when(productRepository.findAll()).thenReturn(products);

        // When
        PageResponseDTO<ProductResponseDTO> result = productService.getAllProducts(0, 10, null, "electronics");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);

        verify(productRepository).findAll();
    }

    @Test
    @DisplayName("Should filter by name and category together")
    void shouldFilterByNameAndCategory() {
        // Given
        List<Product> products = Arrays.asList(product1, product2);
        when(productRepository.findAll()).thenReturn(products);

        // When
        PageResponseDTO<ProductResponseDTO> result = productService.getAllProducts(0, 10, "Product 2", "electronics");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Product 2");

        verify(productRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty when filter finds no matches")
    void shouldReturnEmptyWhenFilterFindsNoMatches() {
        // Given
        List<Product> products = Arrays.asList(product1, product2);
        when(productRepository.findAll()).thenReturn(products);

        // When
        PageResponseDTO<ProductResponseDTO> result = productService.getAllProducts(0, 10, "Nonexistent", null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.isEmpty()).isTrue();

        verify(productRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty recommended products when only one product in category")
    void shouldReturnEmptyRecommendedProductsWhenOnlyOneProductInCategory() {
        // Given
        when(productRepository.findById("3")).thenReturn(Optional.of(product3));
        when(productRepository.findByCategory("fashion")).thenReturn(Arrays.asList(product3));

        // When
        PageResponseDTO<ProductResponseDTO> result = productService.getRecommendedProducts("3", 0, 10);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.isEmpty()).isTrue();

        verify(productRepository).findById("3");
        verify(productRepository).findByCategory("fashion");
    }

    @Test
    @DisplayName("Should paginate recommended products correctly")
    void shouldPaginateRecommendedProductsCorrectly() {
        // Given
        Product product4 = new Product();
        product4.setId("4");
        product4.setName("Product 4");
        product4.setCategory("electronics");

        when(productRepository.findById("1")).thenReturn(Optional.of(product1));
        when(productRepository.findByCategory("electronics")).thenReturn(Arrays.asList(product1, product2, product4));

        // When
        PageResponseDTO<ProductResponseDTO> result = productService.getRecommendedProducts("1", 0, 1);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.isFirst()).isTrue();
        assertThat(result.isLast()).isFalse();

        verify(productRepository).findById("1");
        verify(productRepository).findByCategory("electronics");
    }

    @Test
    @DisplayName("Should paginate products by category correctly")
    void shouldPaginateProductsByCategoryCorrectly() {
        // Given
        List<Product> products = Arrays.asList(product1, product2);
        when(productRepository.findByCategory("electronics")).thenReturn(products);

        // When
        PageResponseDTO<ProductResponseDTO> result = productService.getProductsByCategory("electronics", 1, 1);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getPageNumber()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.isFirst()).isFalse();
        assertThat(result.isLast()).isTrue();

        verify(productRepository).findByCategory("electronics");
    }

    @Test
    @DisplayName("Should return empty when category has no products")
    void shouldReturnEmptyWhenCategoryHasNoProducts() {
        // Given
        when(productRepository.findByCategory("nonexistent")).thenReturn(Arrays.asList());

        // When
        PageResponseDTO<ProductResponseDTO> result = productService.getProductsByCategory("nonexistent", 0, 10);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.isEmpty()).isTrue();
        assertThat(result.isFirst()).isTrue();

        verify(productRepository).findByCategory("nonexistent");
    }

    @Test
    @DisplayName("Should handle page 1 for getProductsByCategory")
    void shouldHandlePage1ForGetProductsByCategory() {
        // Given - setup com múltiplos produtos para ter mais de 1 página
        List<Product> products = Arrays.asList(
            Product.builder().id("1").name("Product 1").category("electronics")
                .price(new BigDecimal("100.00")).quantity(10).active(true).build(),
            Product.builder().id("2").name("Product 2").category("electronics")
                .price(new BigDecimal("200.00")).quantity(5).active(true).build(),
            Product.builder().id("3").name("Product 3").category("electronics")
                .price(new BigDecimal("300.00")).quantity(15).active(true).build()
        );
        when(productRepository.findByCategory("electronics")).thenReturn(products);

        // When - pedir página 1 com tamanho 2 (vai ter 2 páginas)
        PageResponseDTO<ProductResponseDTO> result = productService.getProductsByCategory("electronics", 1, 2);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isFirst()).isFalse();  // página 1 não é a primeira
        assertThat(result.getPageNumber()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1); // última página tem 1 item

        verify(productRepository).findByCategory("electronics");
    }

    @Test
    @DisplayName("Should handle page 1 for getRecommendedProducts with single item")
    void shouldHandlePage1ForGetRecommendedProducts() {
        // Given - produto base
        Product baseProduct = Product.builder()
                .id("1")
                .name("Product 1")
                .category("electronics")
                .price(new BigDecimal("100.00"))
                .quantity(10)
                .active(true)
                .build();
        
        // Produtos recomendados
        Product recommended = Product.builder()
                .id("2")
                .name("Product 2")
                .category("electronics")
                .price(new BigDecimal("200.00"))
                .quantity(5)
                .active(true)
                .build();

        when(productRepository.findById("1")).thenReturn(Optional.of(baseProduct));
        when(productRepository.findByCategory("electronics")).thenReturn(Arrays.asList(baseProduct, recommended));

        // When - pedir página 1 (não primeira)
        PageResponseDTO<ProductResponseDTO> result = productService.getRecommendedProducts("1", 1, 10);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isFirst()).isFalse();
        assertThat(result.getPageNumber()).isEqualTo(1);

        verify(productRepository).findById("1");
        verify(productRepository).findByCategory("electronics");
    }
}
