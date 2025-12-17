package com.mercadolivre.product_api.domain.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

@Getter
public class ProductViewedEvent extends ApplicationEvent {
    
    private final String productId;
    private final String productName;
    private final String category;
    private final LocalDateTime viewedAt;
    private final String requestId;
    
    public ProductViewedEvent(Object source, String productId, String productName, 
                              String category, String requestId) {
        super(source);
        this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.viewedAt = LocalDateTime.now();
        this.requestId = requestId;
    }
}
