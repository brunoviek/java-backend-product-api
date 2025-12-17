package com.mercadolivre.product_api.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mercadolivre.product_api.application.dto.PageResponseDTO;
import com.mercadolivre.product_api.application.dto.ProductImageDTO;
import com.mercadolivre.product_api.application.service.IProductImageService;
import com.mercadolivre.product_api.domain.dto.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Imagens", description = "Endpoints para consulta de imagens de produtos")
@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class ProductImageController {

    private final IProductImageService productImageService;

    @Operation(summary = "Buscar imagens por produto com paginação", 
               description = "Retorna as imagens associadas a um produto específico, ordenadas por displayOrder")
    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<PageResponseDTO<ProductImageDTO>>> getImagesByProductId(
            @Parameter(description = "ID do produto") @PathVariable String productId,
            @Parameter(description = "Número da página (começa em 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Quantidade de itens por página (máximo 50)") @RequestParam(defaultValue = "20") int size) {
        if (size > 50) size = 50;
        PageResponseDTO<ProductImageDTO> data = productImageService.getImagesByProductId(productId, page, size);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

}
