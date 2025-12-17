package com.mercadolivre.product_api.infrastructure.exception;

import com.mercadolivre.product_api.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private WebRequest webRequest;

    @Test
    @DisplayName("Should handle ResourceNotFoundException and return 404")
    void shouldHandleResourceNotFoundException() {
        // Given
        ResourceNotFoundException exception = new ResourceNotFoundException("Product", "id", "123");
        when(webRequest.getDescription(false)).thenReturn("uri=/api/v1/products/123");

        // When
        var response = exceptionHandler.handleResourceNotFoundException(exception, webRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getError()).isEqualTo("Not Found");
        assertThat(response.getBody().getMessage()).contains("Product not found with id: 123");
        assertThat(response.getBody().getPath()).isEqualTo("/api/v1/products/123");
    }

    @Test
    @DisplayName("Should handle generic Exception and return 500")
    void shouldHandleGenericException() {
        // Given
        Exception exception = new RuntimeException("Unexpected error");
        when(webRequest.getDescription(false)).thenReturn("uri=/api/v1/products");

        // When
        var response = exceptionHandler.handleGlobalException(exception, webRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getStatus()).isEqualTo(500);
        assertThat(response.getBody().getError()).isEqualTo("Internal Server Error");
        assertThat(response.getBody().getMessage()).isEqualTo("An unexpected error occurred. Please try again later.");
        assertThat(response.getBody().getPath()).isEqualTo("/api/v1/products");
    }

    @Test
    @DisplayName("Should include timestamp in error response")
    void shouldIncludeTimestampInErrorResponse() {
        // Given
        ResourceNotFoundException exception = new ResourceNotFoundException("Product", "id", "123");
        when(webRequest.getDescription(false)).thenReturn("uri=/api/v1/products/123");

        // When
        var response = exceptionHandler.handleResourceNotFoundException(exception, webRequest);

        // Then
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }
}
