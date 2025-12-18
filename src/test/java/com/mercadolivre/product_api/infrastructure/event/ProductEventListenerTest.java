package com.mercadolivre.product_api.infrastructure.event;

import com.mercadolivre.product_api.domain.event.ProductViewedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductEventListener Tests")
class ProductEventListenerTest {

    @InjectMocks
    private ProductEventListener eventListener;

    private ProductViewedEvent event;

    @BeforeEach
    void setUp() {
        event = new ProductViewedEvent(
                this,
                "prod1",
                "Test Product",
                "electronics",
                "req123"
        );
    }

    @Test
    @DisplayName("Should handle product analytics event")
    void shouldHandleProductAnalyticsEvent() {
        // When
        eventListener.handleProductAnalytics(event);

        // Then
        int viewCount = eventListener.getProductViewCount("prod1");
        assertThat(viewCount).isEqualTo(1);
    }

    @Test
    @DisplayName("Should increment product view count multiple times")
    void shouldIncrementProductViewCountMultipleTimes() {
        // When
        eventListener.handleProductAnalytics(event);
        eventListener.handleProductAnalytics(event);
        eventListener.handleProductAnalytics(event);

        // Then
        int viewCount = eventListener.getProductViewCount("prod1");
        assertThat(viewCount).isEqualTo(3);
    }

    @Test
    @DisplayName("Should handle category metrics event")
    void shouldHandleCategoryMetricsEvent() {
        // When
        eventListener.handleCategoryMetrics(event);

        // Then
        int viewCount = eventListener.getCategoryViewCount("electronics");
        assertThat(viewCount).isEqualTo(1);
    }

    @Test
    @DisplayName("Should increment category view count multiple times")
    void shouldIncrementCategoryViewCountMultipleTimes() {
        // When
        eventListener.handleCategoryMetrics(event);
        eventListener.handleCategoryMetrics(event);

        // Then
        int viewCount = eventListener.getCategoryViewCount("electronics");
        assertThat(viewCount).isEqualTo(2);
    }

    @Test
    @DisplayName("Should handle audit log event")
    void shouldHandleAuditLogEvent() {
        // When & Then (no exception should be thrown)
        eventListener.handleAuditLog(event);
    }

    @Test
    @DisplayName("Should return zero for product with no views")
    void shouldReturnZeroForProductWithNoViews() {
        // When
        int viewCount = eventListener.getProductViewCount("prod999");

        // Then
        assertThat(viewCount).isZero();
    }

    @Test
    @DisplayName("Should return zero for category with no views")
    void shouldReturnZeroForCategoryWithNoViews() {
        // When
        int viewCount = eventListener.getCategoryViewCount("nonexistent");

        // Then
        assertThat(viewCount).isZero();
    }

    @Test
    @DisplayName("Should track different products independently")
    void shouldTrackDifferentProductsIndependently() {
        // Given
        ProductViewedEvent event2 = new ProductViewedEvent(
                this, "prod2", "Product 2", "fashion", "req456"
        );

        // When
        eventListener.handleProductAnalytics(event);
        eventListener.handleProductAnalytics(event);
        eventListener.handleProductAnalytics(event2);

        // Then
        assertThat(eventListener.getProductViewCount("prod1")).isEqualTo(2);
        assertThat(eventListener.getProductViewCount("prod2")).isEqualTo(1);
    }

    @Test
    @DisplayName("Should track different categories independently")
    void shouldTrackDifferentCategoriesIndependently() {
        // Given
        ProductViewedEvent fashionEvent = new ProductViewedEvent(
                this, "prod2", "Product 2", "fashion", "req456"
        );

        // When
        eventListener.handleCategoryMetrics(event);
        eventListener.handleCategoryMetrics(event);
        eventListener.handleCategoryMetrics(fashionEvent);

        // Then
        assertThat(eventListener.getCategoryViewCount("electronics")).isEqualTo(2);
        assertThat(eventListener.getCategoryViewCount("fashion")).isEqualTo(1);
    }

    @Test
    @DisplayName("Should handle interrupted exception in analytics processing")
    void shouldHandleInterruptedExceptionInAnalytics() {
        // Given
        Thread.currentThread().interrupt();

        // When & Then (should not throw exception)
        eventListener.handleProductAnalytics(event);
        
        // Verify thread was interrupted
        assertThat(Thread.interrupted()).isTrue();
    }

    @Test
    @DisplayName("Should handle interrupted exception in category metrics")
    void shouldHandleInterruptedExceptionInCategoryMetrics() {
        // Given
        Thread.currentThread().interrupt();

        // When & Then (should not throw exception)
        eventListener.handleCategoryMetrics(event);
        
        // Verify thread was interrupted
        assertThat(Thread.interrupted()).isTrue();
    }

    @Test
    @DisplayName("Should handle interrupted exception in audit log")
    void shouldHandleInterruptedExceptionInAuditLog() {
        // Given
        Thread.currentThread().interrupt();

        // When & Then (should not throw exception)
        eventListener.handleAuditLog(event);
        
        // Verify thread was interrupted
        assertThat(Thread.interrupted()).isTrue();
    }
}
