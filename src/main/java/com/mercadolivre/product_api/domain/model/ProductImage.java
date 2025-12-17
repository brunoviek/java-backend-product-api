package com.mercadolivre.product_api.domain.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImage implements Serializable {

    private String id;
    private String productId;
    private String url;
    private String altText;
    private Boolean isPrimary;
    private Integer displayOrder;

}
