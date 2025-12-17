package com.mercadolivre.product_api.domain.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ProductViewedEvent Tests")
class ProductViewedEventTest {

    @Test
    @DisplayName("Should create event with all properties")
    void shouldCreateEventWithAllProperties() {
        // Given
        Object source = this;
        String productId = "prod1";
        String productName = "Test Product";
        String category = "electronics";
        String requestId = "req123";

        // When
        ProductViewedEvent event = new ProductViewedEvent(source, productId, productName, category, requestId);

        // Then
        assertThat(event.getSource()).isEqualTo(source);
        assertThat(event.getProductId()).isEqualTo(productId);
        assertThat(event.getProductName()).isEqualTo(productName);
        assertThat(event.getCategory()).isEqualTo(category);
        assertThat(event.getRequestId()).isEqualTo(requestId);
        assertThat(event.getViewedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should set viewedAt timestamp automatically")
    void shouldSetViewedAtTimestampAutomatically() {
        // When
        ProductViewedEvent event = new ProductViewedEvent(this, "prod1", "Product", "cat", "req1");

        // Then
        assertThat(event.getViewedAt()).isNotNull();
        assertThat(event.getViewedAt()).isBeforeOrEqualTo(java.time.LocalDateTime.now());
    }

    @Test
    @DisplayName("Should handle null requestId")
    void shouldHandleNullRequestId() {
        // When
        ProductViewedEvent event = new ProductViewedEvent(this, "prod1", "Product", "cat", null);

        // Then
        assertThat(event.getRequestId()).isNull();
        assertThat(event.getProductId()).isEqualTo("prod1");
    }
}
