package com.mercadolivre.product_api.application.service;

import com.mercadolivre.product_api.application.dto.CategoryDTO;
import com.mercadolivre.product_api.application.dto.PageResponseDTO;
import com.mercadolivre.product_api.domain.exception.ResourceNotFoundException;
import com.mercadolivre.product_api.domain.model.Category;
import com.mercadolivre.product_api.domain.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryService Tests")
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category1;
    private Category category2;

    @BeforeEach
    void setUp() {
        category1 = new Category();
        category1.setId("1");
        category1.setName("Electronics");
        category1.setDescription("Electronic products");
        category1.setSlug("electronics");
        category1.setProductCount(100);

        category2 = new Category();
        category2.setId("2");
        category2.setName("Fashion");
        category2.setDescription("Fashion products");
        category2.setSlug("fashion");
        category2.setProductCount(50);
    }

    @Test
    @DisplayName("Should return all categories with pagination")
    void shouldReturnAllCategoriesWithPagination() {
        // Given
        List<Category> categories = Arrays.asList(category1, category2);
        when(categoryRepository.findAll()).thenReturn(categories);

        // When
        PageResponseDTO<CategoryDTO> result = categoryService.getAllCategories(0, 10);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.isFirst()).isTrue();
        assertThat(result.isLast()).isTrue();

        verify(categoryRepository).findAll();
    }

    @Test
    @DisplayName("Should return category by ID")
    void shouldReturnCategoryById() {
        // Given
        when(categoryRepository.findById("1")).thenReturn(Optional.of(category1));

        // When
        CategoryDTO result = categoryService.getCategoryById("1");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("1");
        assertThat(result.getName()).isEqualTo("Electronics");
        assertThat(result.getSlug()).isEqualTo("electronics");

        verify(categoryRepository).findById("1");
    }

    @Test
    @DisplayName("Should throw exception when category not found by ID")
    void shouldThrowExceptionWhenCategoryNotFoundById() {
        // Given
        when(categoryRepository.findById("999")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> categoryService.getCategoryById("999"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category not found with id: 999");

        verify(categoryRepository).findById("999");
    }

    @Test
    @DisplayName("Should return category by slug")
    void shouldReturnCategoryBySlug() {
        // Given
        when(categoryRepository.findBySlug("electronics")).thenReturn(Optional.of(category1));

        // When
        CategoryDTO result = categoryService.getCategoryBySlug("electronics");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("1");
        assertThat(result.getSlug()).isEqualTo("electronics");

        verify(categoryRepository).findBySlug("electronics");
    }

    @Test
    @DisplayName("Should throw exception when category not found by slug")
    void shouldThrowExceptionWhenCategoryNotFoundBySlug() {
        // Given
        when(categoryRepository.findBySlug("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> categoryService.getCategoryBySlug("nonexistent"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category not found with slug: nonexistent");

        verify(categoryRepository).findBySlug("nonexistent");
    }

    @Test
    @DisplayName("Should paginate categories correctly")
    void shouldPaginateCategoriesCorrectly() {
        // Given
        List<Category> categories = Arrays.asList(category1, category2);
        when(categoryRepository.findAll()).thenReturn(categories);

        // When
        PageResponseDTO<CategoryDTO> result = categoryService.getAllCategories(0, 1);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.isFirst()).isTrue();
        assertThat(result.isLast()).isFalse();

        verify(categoryRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty page when page number is beyond available pages")
    void shouldReturnEmptyPageWhenPageNumberIsBeyondAvailablePages() {
        // Given
        List<Category> categories = Arrays.asList(category1);
        when(categoryRepository.findAll()).thenReturn(categories);

        // When
        PageResponseDTO<CategoryDTO> result = categoryService.getAllCategories(10, 10);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.isEmpty()).isTrue();

        verify(categoryRepository).findAll();
    }

    @Test
    @DisplayName("Should return last page when requesting second page")
    void shouldReturnLastPageWhenRequestingSecondPage() {
        // Given
        List<Category> categories = Arrays.asList(category1, category2);
        when(categoryRepository.findAll()).thenReturn(categories);

        // When
        PageResponseDTO<CategoryDTO> result = categoryService.getAllCategories(1, 1);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getPageNumber()).isEqualTo(1);
        assertThat(result.isFirst()).isFalse();
        assertThat(result.isLast()).isTrue();

        verify(categoryRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty page when no categories exist")
    void shouldReturnEmptyPageWhenNoCategoriesExist() {
        // Given
        when(categoryRepository.findAll()).thenReturn(Arrays.asList());

        // When
        PageResponseDTO<CategoryDTO> result = categoryService.getAllCategories(0, 10);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.isEmpty()).isTrue();
        assertThat(result.isFirst()).isTrue();
        assertThat(result.isLast()).isTrue();

        verify(categoryRepository).findAll();
    }
}
