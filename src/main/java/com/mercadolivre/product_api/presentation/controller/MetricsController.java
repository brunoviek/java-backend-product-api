package com.mercadolivre.product_api.presentation.controller;

import com.mercadolivre.product_api.domain.dto.ApiResponse;
import com.mercadolivre.product_api.infrastructure.event.ProductEventListener;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "Métricas", description = "Endpoints para consulta de métricas de visualização (Demo Async Events)")
@RestController
@RequestMapping("/api/v1/metrics")
@RequiredArgsConstructor
public class MetricsController {

    private final ProductEventListener eventListener;

    @Operation(summary = "Obter visualizações de um produto", 
               description = "Retorna o número de visualizações de um produto específico (processado de forma assíncrona)")
    @GetMapping("/products/{productId}/views")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProductViews(
            @Parameter(description = "ID do produto") @PathVariable String productId) {
        
        int viewCount = eventListener.getProductViewCount(productId);
        
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("productId", productId);
        metrics.put("viewCount", viewCount);
        metrics.put("note", "Métricas processadas de forma assíncrona via Spring Events");
        
        return ResponseEntity.ok(ApiResponse.success(metrics, "Product view metrics retrieved successfully"));
    }

    @Operation(summary = "Obter visualizações de uma categoria", 
               description = "Retorna o número de visualizações de produtos de uma categoria (processado de forma assíncrona)")
    @GetMapping("/categories/{category}/views")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCategoryViews(
            @Parameter(description = "Nome da categoria") @PathVariable String category) {
        
        int viewCount = eventListener.getCategoryViewCount(category);
        
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("category", category);
        metrics.put("viewCount", viewCount);
        metrics.put("note", "Métricas processadas de forma assíncrona via Spring Events");
        
        return ResponseEntity.ok(ApiResponse.success(metrics, "Category view metrics retrieved successfully"));
    }
}
