package com.mercadolivre.product_api.application.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.mercadolivre.product_api.application.dto.PageResponseDTO;
import com.mercadolivre.product_api.application.dto.ProductResponseDTO;
import com.mercadolivre.product_api.domain.event.ProductViewedEvent;
import com.mercadolivre.product_api.domain.exception.ResourceNotFoundException;
import com.mercadolivre.product_api.domain.model.Product;
import com.mercadolivre.product_api.domain.repository.ProductRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {

    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @CircuitBreaker(name = "productService", fallbackMethod = "getProductByIdFallback")
    @Retry(name = "productService")
    @Cacheable(value = "products", key = "#id")
    public ProductResponseDTO getProductById(String id) {
        log.info("Getting product by id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        // Publica evento assíncrono de visualização
        String requestId = org.slf4j.MDC.get("requestId");
        eventPublisher.publishEvent(new ProductViewedEvent(
            this, product.getId(), product.getName(), product.getCategory(), requestId
        ));
        log.info("Product viewed event published for: {}", product.getName());

        return mapToResponseDTO(product);
    }

    @Override
    @CircuitBreaker(name = "productService", fallbackMethod = "getAllProductsFallback")
    @Retry(name = "productService")
    @Cacheable(value = "allProducts", key = "#page + '-' + #size + '-' + #name + '-' + #category")
    public PageResponseDTO<ProductResponseDTO> getAllProducts(int page, int size, String name, String category) {
        log.info("Getting all products - page: {}, size: {}, name: {}, category: {}", page, size, name, category);

        List<Product> allProducts = productRepository.findAll();
        
        // Aplicar filtros
        if (name != null && !name.trim().isEmpty()) {
            String nameLower = name.toLowerCase();
            allProducts = allProducts.stream()
                    .filter(p -> p.getName().toLowerCase().contains(nameLower))
                    .collect(Collectors.toList());
        }
        
        if (category != null && !category.trim().isEmpty()) {
            allProducts = allProducts.stream()
                    .filter(p -> p.getCategory().equalsIgnoreCase(category))
                    .collect(Collectors.toList());
        }
        
        int totalElements = allProducts.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int start = page * size;
        int end = Math.min(start + size, totalElements);

        List<ProductResponseDTO> content = allProducts.stream()
                .skip(start)
                .limit(size)
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());

        return PageResponseDTO.<ProductResponseDTO>builder()
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
    @CircuitBreaker(name = "productService", fallbackMethod = "getProductsByCategoryFallback")
    @Retry(name = "productService")
    @Cacheable(value = "productsByCategory", key = "#category + '-' + #page + '-' + #size")
    public PageResponseDTO<ProductResponseDTO> getProductsByCategory(String category, int page, int size) {
        log.info("Getting products by category: {} - page: {}, size: {}", category, page, size);

        List<Product> allProducts = productRepository.findByCategory(category);
        
        int totalElements = allProducts.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int start = page * size;

        List<ProductResponseDTO> content = allProducts.stream()
                .skip(start)
                .limit(size)
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());

        return PageResponseDTO.<ProductResponseDTO>builder()
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
    @CircuitBreaker(name = "productService", fallbackMethod = "getRecommendedProductsFallback")
    @Retry(name = "productService")
    @Cacheable(value = "recommendedProducts", key = "#productId + '-' + #page + '-' + #size")
    public PageResponseDTO<ProductResponseDTO> getRecommendedProducts(String productId, int page, int size) {
        log.info("Getting recommended products for: {} - page: {}, size: {}", productId, page, size);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        List<Product> allRecommended = productRepository.findByCategory(product.getCategory())
                .stream()
                .filter(p -> !p.getId().equals(productId))
                .collect(Collectors.toList());
        
        int totalElements = allRecommended.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int start = page * size;

        List<ProductResponseDTO> content = allRecommended.stream()
                .skip(start)
                .limit(size)
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());

        return PageResponseDTO.<ProductResponseDTO>builder()
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

    // Fallback methods
    private ProductResponseDTO getProductByIdFallback(String id, Exception e) {
        log.error("Fallback: Failed to get product by id: {} - Error: {}", id, e.getMessage());
        // Re-throw ResourceNotFoundException to maintain 404 status
        if (e instanceof ResourceNotFoundException) {
            throw (ResourceNotFoundException) e;
        }
        throw new RuntimeException("Service temporarily unavailable. Please try again later.");
    }

    private PageResponseDTO<ProductResponseDTO> getAllProductsFallback(int page, int size, String name, String category, Exception e) {
        log.error("Fallback: Failed to get all products - Error: {}", e.getMessage());
        return PageResponseDTO.<ProductResponseDTO>builder()
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

    private PageResponseDTO<ProductResponseDTO> getProductsByCategoryFallback(String category, int page, int size, Exception e) {
        log.error("Fallback: Failed to get products by category: {} - Error: {}", category, e.getMessage());
        return PageResponseDTO.<ProductResponseDTO>builder()
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

    private PageResponseDTO<ProductResponseDTO> getRecommendedProductsFallback(String productId, int page, int size, Exception e) {
        log.error("Fallback: Failed to get recommended products for: {} - Error: {}", productId, e.getMessage());
        return PageResponseDTO.<ProductResponseDTO>builder()
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

    private ProductResponseDTO mapToResponseDTO(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .category(product.getCategory())
                .active(product.getActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

}
