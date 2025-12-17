package com.mercadolivre.product_api.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageDTO {

    private String id;
    private String productId;
    private String url;
    private String altText;
    private Boolean isPrimary;
    private Integer displayOrder;

}
