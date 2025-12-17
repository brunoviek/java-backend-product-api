package com.mercadolivre.product_api.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mercadolivre.product_api.application.dto.CategoryDTO;
import com.mercadolivre.product_api.application.dto.PageResponseDTO;
import com.mercadolivre.product_api.application.service.ICategoryService;
import com.mercadolivre.product_api.domain.dto.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Categorias", description = "Endpoints para consulta de categorias de produtos")
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final ICategoryService categoryService;

    @Operation(summary = "Listar todas as categorias com paginação", 
               description = "Retorna as categorias disponíveis no sistema com paginação")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDTO<CategoryDTO>>> getAllCategories(
            @Parameter(description = "Número da página (começa em 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Quantidade de itens por página (máximo 50)") @RequestParam(defaultValue = "20") int size) {
        if (size > 50) size = 50;
        PageResponseDTO<CategoryDTO> data = categoryService.getAllCategories(page, size);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @Operation(summary = "Buscar categoria por ID", 
               description = "Retorna os detalhes de uma categoria específica pelo ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryDTO>> getCategoryById(
            @Parameter(description = "ID da categoria") @PathVariable String id) {
        CategoryDTO data = categoryService.getCategoryById(id);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @Operation(summary = "Buscar categoria por slug", 
               description = "Retorna os detalhes de uma categoria específica pelo slug (URL amigável)")
    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<CategoryDTO>> getCategoryBySlug(
            @Parameter(description = "Slug da categoria (ex: eletronicos)") @PathVariable String slug) {
        CategoryDTO data = categoryService.getCategoryBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

}
