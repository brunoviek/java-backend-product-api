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
public class Category implements Serializable {

    private String id;
    private String name;
    private String description;
    private String slug;
    private Integer productCount;

}
