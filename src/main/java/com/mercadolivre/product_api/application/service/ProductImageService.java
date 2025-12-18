package com.mercadolivre.product_api.application.service;

import com.mercadolivre.product_api.application.dto.PageResponseDTO;
import com.mercadolivre.product_api.application.dto.ProductImageDTO;
import com.mercadolivre.product_api.domain.model.ProductImage;
import com.mercadolivre.product_api.domain.repository.ProductImageRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductImageService implements IProductImageService {

    private final ProductImageRepository productImageRepository;

    @Override
    @CircuitBreaker(name = "productImageService", fallbackMethod = "getImagesByProductIdFallback")
    @Retry(name = "productImageService")
    @Cacheable(value = "productImages", key = "#productId + '-' + #page + '-' + #size")
    public PageResponseDTO<ProductImageDTO> getImagesByProductId(String productId, int page, int size) {
        log.info("Getting images for product: {} - page: {}, size: {}", productId, page, size);

        List<ProductImage> allImages = productImageRepository.findByProductId(productId)
                .stream()
                .sorted(Comparator.comparing(ProductImage::getDisplayOrder, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
        
        int totalElements = allImages.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int start = page * size;

        List<ProductImageDTO> content = allImages.stream()
                .skip(start)
                .limit(size)
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return PageResponseDTO.<ProductImageDTO>builder()
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
    @lombok.Generated
    private PageResponseDTO<ProductImageDTO> getImagesByProductIdFallback(String productId, int page, int size, Exception e) {
        log.error("Fallback: Failed to get images for product: {} - Error: {}", productId, e.getMessage());
        return PageResponseDTO.<ProductImageDTO>builder()
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

    private ProductImageDTO mapToDTO(ProductImage image) {
        return ProductImageDTO.builder()
                .id(image.getId())
                .productId(image.getProductId())
                .url(image.getUrl())
                .altText(image.getAltText())
                .isPrimary(image.getIsPrimary())
                .displayOrder(image.getDisplayOrder())
                .build();
    }

}
