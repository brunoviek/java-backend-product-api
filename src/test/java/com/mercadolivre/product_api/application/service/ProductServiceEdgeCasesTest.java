package com.mercadolivre.product_api.application.service;

import com.mercadolivre.product_api.domain.exception.ResourceNotFoundException;
import com.mercadolivre.product_api.domain.model.Product;
import com.mercadolivre.product_api.domain.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceEdgeCasesTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ProductService productService;

    @Test
    void shouldHandleEmptyNameFilter() {
        Product product1 = createProduct("1", "Product 1", "electronics");
        Product product2 = createProduct("2", "Product 2", "electronics");
        
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));
        
        // Test with empty string
        var result1 = productService.getAllProducts(0, 10, "", null);
        assertThat(result1.getContent()).hasSize(2);
        
        // Test with whitespace only
        var result2 = productService.getAllProducts(0, 10, "   ", null);
        assertThat(result2.getContent()).hasSize(2);
        
        verify(productRepository, times(2)).findAll();
    }

    @Test
    void shouldHandleEmptyCategoryFilter() {
        Product product1 = createProduct("1", "Product 1", "electronics");
        Product product2 = createProduct("2", "Product 2", "fashion");
        
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));
        
        // Test with empty string
        var result1 = productService.getAllProducts(0, 10, null, "");
        assertThat(result1.getContent()).hasSize(2);
        
        // Test with whitespace only
        var result2 = productService.getAllProducts(0, 10, null, "   ");
        assertThat(result2.getContent()).hasSize(2);
        
        verify(productRepository, times(2)).findAll();
    }

    @Test
    void shouldFilterByCaseInsensitiveCategory() {
        Product product1 = createProduct("1", "Product 1", "Electronics");
        Product product2 = createProduct("2", "Product 2", "Fashion");
        
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));
        
        // Test case insensitive
        var result1 = productService.getAllProducts(0, 10, null, "ELECTRONICS");
        assertThat(result1.getContent()).hasSize(1);
        assertThat(result1.getContent().get(0).getCategory()).isEqualTo("Electronics");
        
        var result2 = productService.getAllProducts(0, 10, null, "electronics");
        assertThat(result2.getContent()).hasSize(1);
        
        verify(productRepository, times(2)).findAll();
    }

    @Test
    void shouldFilterByPartialName() {
        Product product1 = createProduct("1", "Smartphone Samsung", "electronics");
        Product product2 = createProduct("2", "Laptop Samsung", "electronics");
        Product product3 = createProduct("3", "iPhone Apple", "electronics");
        
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2, product3));
        
        var result = productService.getAllProducts(0, 10, "Samsung", null);
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).allMatch(p -> p.getName().contains("Samsung"));
        
        verify(productRepository).findAll();
    }

    @Test
    void shouldCombineNameAndCategoryFilters() {
        Product product1 = createProduct("1", "Samsung TV", "electronics");
        Product product2 = createProduct("2", "Samsung Shirt", "fashion");
        Product product3 = createProduct("3", "LG TV", "electronics");
        
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2, product3));
        
        var result = productService.getAllProducts(0, 10, "Samsung", "electronics");
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo("1");
        
        verify(productRepository).findAll();
    }

    @Test
    void shouldHandleZeroSizeInPagination() {
        Product product1 = createProduct("1", "Product 1", "electronics");
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1));
        
        var result = productService.getAllProducts(0, 1, null, null);
        assertThat(result.getPageSize()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void shouldHandleTotalPagesCalculationWithOneElement() {
        Product product1 = createProduct("1", "Product 1", "electronics");
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1));
        
        var result = productService.getAllProducts(0, 10, null, null);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.isFirst()).isTrue();
        assertThat(result.isLast()).isTrue();
    }

    @Test
    void shouldCalculateCorrectEndIndexForPartialPage() {
        Product product1 = createProduct("1", "Product 1", "electronics");
        Product product2 = createProduct("2", "Product 2", "electronics");
        Product product3 = createProduct("3", "Product 3", "electronics");
        
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2, product3));
        
        // Request page with size larger than remaining elements
        var result = productService.getAllProducts(1, 5, null, null);
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(3);
    }

    @Test
    void shouldHandleRecommendedProductsExcludingCurrent() {
        Product product1 = createProduct("1", "Product 1", "electronics");
        Product product2 = createProduct("2", "Product 2", "electronics");
        Product product3 = createProduct("3", "Product 3", "electronics");
        
        when(productRepository.findById("1")).thenReturn(Optional.of(product1));
        when(productRepository.findByCategory("electronics"))
                .thenReturn(Arrays.asList(product1, product2, product3));
        
        var result = productService.getRecommendedProducts("1", 0, 10);
        
        // Should exclude product with id "1"
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).noneMatch(p -> p.getId().equals("1"));
        assertThat(result.getContent()).anyMatch(p -> p.getId().equals("2"));
        assertThat(result.getContent()).anyMatch(p -> p.getId().equals("3"));
    }

    @Test
    void shouldHandleCaseInsensitiveNameSearch() {
        Product product1 = createProduct("1", "SAMSUNG GALAXY", "electronics");
        Product product2 = createProduct("2", "samsung galaxy s21", "electronics");
        
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));
        
        var result1 = productService.getAllProducts(0, 10, "SAMSUNG", null);
        assertThat(result1.getContent()).hasSize(2);
        
        var result2 = productService.getAllProducts(0, 10, "samsung", null);
        assertThat(result2.getContent()).hasSize(2);
        
        var result3 = productService.getAllProducts(0, 10, "Galaxy", null);
        assertThat(result3.getContent()).hasSize(2);
    }

    private Product createProduct(String id, String name, String category) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setDescription("Description for " + name);
        product.setPrice(new BigDecimal("100.00"));
        product.setQuantity(10);
        product.setCategory(category);
        product.setActive(true);
        return product;
    }
}
