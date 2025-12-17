package com.mercadolivre.product_api.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDTO {

    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private String category;
    private Boolean active;

}
