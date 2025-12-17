package com.mercadolivre.product_api.infrastructure.event;

import com.mercadolivre.product_api.domain.event.ProductViewedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class ProductEventListener {

    // Simulação de armazenamento de métricas
    private final Map<String, AtomicInteger> productViewsCount = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> categoryViewsCount = new ConcurrentHashMap<>();

    @Async
    @EventListener
    public void handleProductAnalytics(ProductViewedEvent event) {
        log.info("[ASYNC-ANALYTICS] Processing analytics for product: {} - RequestID: {} - Thread: {}", 
            event.getProductName(), event.getRequestId(), Thread.currentThread().getName());
        
        try {
            // Simula processamento de analytics
            Thread.sleep(500);
            
            // Incrementa contador de visualizações do produto
            productViewsCount.computeIfAbsent(event.getProductId(), k -> new AtomicInteger(0))
                .incrementAndGet();
            
            log.info("[ASYNC-ANALYTICS] Analytics processed for product: {} - Total views: {}", 
                event.getProductName(), 
                productViewsCount.get(event.getProductId()).get());
                
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Analytics processing interrupted", e);
        }
    }

    @Async
    @EventListener
    public void handleCategoryMetrics(ProductViewedEvent event) {
        log.info("[ASYNC-METRICS] Processing category metrics for: {} - RequestID: {} - Thread: {}", 
            event.getCategory(), event.getRequestId(), Thread.currentThread().getName());
        
        try {
            // Simula processamento de métricas
            Thread.sleep(300);
            
            // Incrementa contador de visualizações da categoria
            categoryViewsCount.computeIfAbsent(event.getCategory(), k -> new AtomicInteger(0))
                .incrementAndGet();
            
            log.info("[ASYNC-METRICS] Category metrics processed: {} - Total views: {}", 
                event.getCategory(),
                categoryViewsCount.get(event.getCategory()).get());
                
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Category metrics processing interrupted", e);
        }
    }

    @Async
    @EventListener
    public void handleAuditLog(ProductViewedEvent event) {
        log.info("[ASYNC-AUDIT] Logging audit for product: {} - RequestID: {} - Thread: {} - ViewedAt: {}", 
            event.getProductId(), event.getRequestId(), Thread.currentThread().getName(), 
            event.getViewedAt());
        
        try {
            // Simula gravação de auditoria
            Thread.sleep(200);
            
            log.info("[ASYNC-AUDIT] Audit log saved for product: {} at {}", 
                event.getProductName(), event.getViewedAt());
                
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Audit log processing interrupted", e);
        }
    }

    // Métodos para consultar métricas (útil para demonstração)
    public int getProductViewCount(String productId) {
        return productViewsCount.getOrDefault(productId, new AtomicInteger(0)).get();
    }

    public int getCategoryViewCount(String category) {
        return categoryViewsCount.getOrDefault(category, new AtomicInteger(0)).get();
    }
}
