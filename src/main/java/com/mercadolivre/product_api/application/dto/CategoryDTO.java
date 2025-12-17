package com.mercadolivre.product_api.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

    private String id;
    private String name;
    private String description;
    private String slug;
    private Integer productCount;

}
