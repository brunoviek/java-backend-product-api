package com.mercadolivre.product_api.application.service;

import com.mercadolivre.product_api.domain.model.Category;
import com.mercadolivre.product_api.domain.repository.CategoryRepository;
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
class CategoryServiceEdgeCasesTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void shouldHandleEmptyCategories() {
        when(categoryRepository.findAll()).thenReturn(Collections.emptyList());
        
        var result = categoryService.getAllCategories(0, 10);
        
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.getTotalPages()).isEqualTo(0);
        assertThat(result.isEmpty()).isTrue();
        assertThat(result.isFirst()).isTrue();
        assertThat(result.isLast()).isTrue();
        
        verify(categoryRepository).findAll();
    }

    @Test
    void shouldHandleSingleCategory() {
        Category category = createCategory("1", "Electronics", "electronics");
        when(categoryRepository.findAll()).thenReturn(Arrays.asList(category));
        
        var result = categoryService.getAllCategories(0, 10);
        
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.isFirst()).isTrue();
        assertThat(result.isLast()).isTrue();
        
        verify(categoryRepository).findAll();
    }

    @Test
    void shouldCalculateTotalPagesCorrectly() {
        Category cat1 = createCategory("1", "Electronics", "electronics");
        Category cat2 = createCategory("2", "Fashion", "fashion");
        Category cat3 = createCategory("3", "Home", "home");
        Category cat4 = createCategory("4", "Sports", "sports");
        Category cat5 = createCategory("5", "Books", "books");
        
        when(categoryRepository.findAll()).thenReturn(Arrays.asList(cat1, cat2, cat3, cat4, cat5));
        
        // Page size 2 should give 3 total pages (5 items / 2 = 2.5 -> 3 pages)
        var result1 = categoryService.getAllCategories(0, 2);
        assertThat(result1.getTotalPages()).isEqualTo(3);
        assertThat(result1.getContent()).hasSize(2);
        assertThat(result1.isFirst()).isTrue();
        assertThat(result1.isLast()).isFalse();
        
        // Page size 3 should give 2 total pages (5 items / 3 = 1.67 -> 2 pages)
        var result2 = categoryService.getAllCategories(0, 3);
        assertThat(result2.getTotalPages()).isEqualTo(2);
        assertThat(result2.getContent()).hasSize(3);
        
        verify(categoryRepository, times(2)).findAll();
    }

    @Test
    void shouldHandlePageBeyondLastPage() {
        Category category = createCategory("1", "Electronics", "electronics");
        when(categoryRepository.findAll()).thenReturn(Arrays.asList(category));
        
        // Request page 10 when there's only 1 page
        var result = categoryService.getAllCategories(10, 10);
        
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.isEmpty()).isTrue();
        assertThat(result.isFirst()).isFalse();
        
        verify(categoryRepository).findAll();
    }

    @Test
    void shouldHandleMiddlePage() {
        Category cat1 = createCategory("1", "Cat1", "cat1");
        Category cat2 = createCategory("2", "Cat2", "cat2");
        Category cat3 = createCategory("3", "Cat3", "cat3");
        Category cat4 = createCategory("4", "Cat4", "cat4");
        Category cat5 = createCategory("5", "Cat5", "cat5");
        
        when(categoryRepository.findAll()).thenReturn(Arrays.asList(cat1, cat2, cat3, cat4, cat5));
        
        var result = categoryService.getAllCategories(1, 2);
        
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getPageNumber()).isEqualTo(1);
        assertThat(result.isFirst()).isFalse();
        assertThat(result.isLast()).isFalse();
        assertThat(result.getContent().get(0).getId()).isEqualTo("3");
        assertThat(result.getContent().get(1).getId()).isEqualTo("4");
        
        verify(categoryRepository).findAll();
    }

    @Test
    void shouldHandleLastPageWithFewerElements() {
        Category cat1 = createCategory("1", "Cat1", "cat1");
        Category cat2 = createCategory("2", "Cat2", "cat2");
        Category cat3 = createCategory("3", "Cat3", "cat3");
        
        when(categoryRepository.findAll()).thenReturn(Arrays.asList(cat1, cat2, cat3));
        
        var result = categoryService.getAllCategories(1, 2);
        
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getPageNumber()).isEqualTo(1);
        assertThat(result.isFirst()).isFalse();
        assertThat(result.isLast()).isTrue();
        assertThat(result.getContent().get(0).getId()).isEqualTo("3");
        
        verify(categoryRepository).findAll();
    }

    @Test
    void shouldMapCategoryToDTOCorrectly() {
        Category category = createCategory("1", "Electronics", "electronics");
        category.setDescription("Electronic products");
        category.setProductCount(100);
        
        when(categoryRepository.findAll()).thenReturn(Arrays.asList(category));
        
        var result = categoryService.getAllCategories(0, 10);
        var dto = result.getContent().get(0);
        
        assertThat(dto.getId()).isEqualTo("1");
        assertThat(dto.getName()).isEqualTo("Electronics");
        assertThat(dto.getSlug()).isEqualTo("electronics");
        assertThat(dto.getDescription()).isEqualTo("Electronic products");
        assertThat(dto.getProductCount()).isEqualTo(100);
        
        verify(categoryRepository).findAll();
    }

    private Category createCategory(String id, String name, String slug) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        category.setSlug(slug);
        category.setDescription("Description for " + name);
        category.setProductCount(0);
        return category;
    }
}
