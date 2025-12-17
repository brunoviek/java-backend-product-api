package com.mercadolivre.product_api.presentation.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mercadolivre.product_api.application.dto.PageResponseDTO;
import com.mercadolivre.product_api.application.dto.ProductResponseDTO;
import com.mercadolivre.product_api.application.service.IProductService;
import com.mercadolivre.product_api.domain.dto.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Produtos", description = "Endpoints para gerenciamento de produtos")
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final IProductService productService;

    @Operation(summary = "Listar todos os produtos com paginação e filtros", 
               description = "Retorna uma lista paginada de produtos com metadados de paginação. Pode filtrar por nome e/ou categoria")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDTO<ProductResponseDTO>>> getAllProducts(
            @Parameter(description = "Número da página (começa em 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Quantidade de itens por página (máximo 50)") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Filtro por nome do produto (busca parcial)") @RequestParam(required = false) String name,
            @Parameter(description = "Filtro por categoria do produto") @RequestParam(required = false) String category) {
        if (size > 50) size = 50;
        PageResponseDTO<ProductResponseDTO> data = productService.getAllProducts(page, size, name, category);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @Operation(summary = "Buscar produto por ID", 
               description = "Retorna os detalhes de um produto específico pelo ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> getProductById(
            @Parameter(description = "ID do produto") @PathVariable String id) {
        ProductResponseDTO data = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @Operation(summary = "Buscar produtos por categoria com paginação", 
               description = "Retorna produtos de uma categoria específica com paginação")
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<PageResponseDTO<ProductResponseDTO>>> getProductsByCategory(
            @Parameter(description = "Slug ou nome da categoria") @PathVariable String category,
            @Parameter(description = "Número da página (começa em 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Quantidade de itens por página (máximo 50)") @RequestParam(defaultValue = "20") int size) {
        if (size > 50) size = 50;
        PageResponseDTO<ProductResponseDTO> data = productService.getProductsByCategory(category, page, size);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @Operation(summary = "Buscar produtos recomendados com paginação", 
               description = "Retorna produtos recomendados baseados na mesma categoria do produto informado")
    @GetMapping("/{id}/recommended")
    public ResponseEntity<ApiResponse<PageResponseDTO<ProductResponseDTO>>> getRecommendedProducts(
            @Parameter(description = "ID do produto base para recomendações") @PathVariable String id,
            @Parameter(description = "Número da página (começa em 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Quantidade de itens por página (máximo 50)") @RequestParam(defaultValue = "20") int size) {
        if (size > 50) size = 50;
        PageResponseDTO<ProductResponseDTO> data = productService.getRecommendedProducts(id, page, size);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

}
