package com.mercadolivre.product_api.application.service;

import com.mercadolivre.product_api.application.dto.CategoryDTO;
import com.mercadolivre.product_api.application.dto.PageResponseDTO;
import com.mercadolivre.product_api.domain.exception.ResourceNotFoundException;
import com.mercadolivre.product_api.domain.model.Category;
import com.mercadolivre.product_api.domain.repository.CategoryRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @CircuitBreaker(name = "categoryService", fallbackMethod = "getAllCategoriesFallback")
    @Retry(name = "categoryService")
    @Cacheable(value = "allCategories", key = "#page + '-' + #size")
    public PageResponseDTO<CategoryDTO> getAllCategories(int page, int size) {
        log.info("Getting all categories - page: {}, size: {}", page, size);

        List<Category> allCategories = categoryRepository.findAll();
        
        int totalElements = allCategories.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int start = page * size;

        List<CategoryDTO> content = allCategories.stream()
                .skip(start)
                .limit(size)
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return PageResponseDTO.<CategoryDTO>builder()
                .content(content)
                .pageNumber(page)
                .pageSize(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .first(page == 0)
                .last(page >= totalPages - 1)
                .empty(content.isEmpty())
                .build();
    }

    @Override
    @CircuitBreaker(name = "categoryService", fallbackMethod = "getCategoryByIdFallback")
    @Retry(name = "categoryService")
    @Cacheable(value = "categories", key = "#id")
    public CategoryDTO getCategoryById(String id) {
        log.info("Getting category by id: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        return mapToDTO(category);
    }
    @Override    @CircuitBreaker(name = "categoryService", fallbackMethod = "getCategoryBySlugFallback")
    @Retry(name = "categoryService")
    @Cacheable(value = "categories", key = "#slug")
    public CategoryDTO getCategoryBySlug(String slug) {
        log.info("Getting category by slug: {}", slug);

        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "slug", slug));

        return mapToDTO(category);
    }

    // Fallback methods
    @lombok.Generated
    private PageResponseDTO<CategoryDTO> getAllCategoriesFallback(int page, int size, Exception e) {
        log.error("Fallback: Failed to get all categories - Error: {}", e.getMessage());
        return PageResponseDTO.<CategoryDTO>builder()
                .content(List.of())
                .pageNumber(page)
                .pageSize(size)
                .totalElements(0)
                .totalPages(0)
                .first(true)
                .last(true)
                .empty(true)
                .build();
    }

    @lombok.Generated
    private CategoryDTO getCategoryByIdFallback(String id, Exception e) {
        log.error("Fallback: Failed to get category by id: {} - Error: {}", id, e.getMessage());
        if (e instanceof ResourceNotFoundException) {
            throw (ResourceNotFoundException) e;
        }
        throw new RuntimeException("Service temporarily unavailable. Please try again later.");
    }

    @lombok.Generated
    private CategoryDTO getCategoryBySlugFallback(String slug, Exception e) {
        log.error("Fallback: Failed to get category by slug: {} - Error: {}", slug, e.getMessage());
        if (e instanceof ResourceNotFoundException) {
            throw (ResourceNotFoundException) e;
        }
        throw new RuntimeException("Service temporarily unavailable. Please try again later.");
    }

    private CategoryDTO mapToDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .slug(category.getSlug())
                .productCount(category.getProductCount())
                .build();
    }

}
