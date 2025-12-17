package com.mercadolivre.product_api.domain.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ApiResponse Tests")
class ApiResponseTest {

    @Test
    @DisplayName("Should create success response with data")
    void shouldCreateSuccessResponseWithData() {
        // Given
        String data = "Test Data";

        // When
        ApiResponse<String> response = ApiResponse.success(data);

        // Then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Success");
        assertThat(response.getData()).isEqualTo(data);
        assertThat(response.getTimestamp()).isNotNull();
        assertThat(response.getError()).isNull();
        assertThat(response.getStatus()).isNull();
        assertThat(response.getPath()).isNull();
    }

    @Test
    @DisplayName("Should create success response with custom message")
    void shouldCreateSuccessResponseWithCustomMessage() {
        // Given
        String data = "Test Data";
        String message = "Custom success message";

        // When
        ApiResponse<String> response = ApiResponse.success(data, message);

        // Then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo(message);
        assertThat(response.getData()).isEqualTo(data);
    }

    @Test
    @DisplayName("Should create error response")
    void shouldCreateErrorResponse() {
        // Given
        String message = "Error message";
        String error = "Not Found";
        Integer status = 404;
        String path = "/api/v1/products/999";

        // When
        ApiResponse<Object> response = ApiResponse.error(message, error, status, path);

        // Then
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo(message);
        assertThat(response.getError()).isEqualTo(error);
        assertThat(response.getStatus()).isEqualTo(status);
        assertThat(response.getPath()).isEqualTo(path);
        assertThat(response.getData()).isNull();
        assertThat(response.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("Should create response using builder")
    void shouldCreateResponseUsingBuilder() {
        // When
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Test")
                .data("Data")
                .timestamp(LocalDateTime.now())
                .build();

        // Then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Test");
        assertThat(response.getData()).isEqualTo("Data");
    }

    @Test
    @DisplayName("Should handle null data in success response")
    void shouldHandleNullDataInSuccessResponse() {
        // When
        ApiResponse<Object> response = ApiResponse.success(null);

        // Then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isNull();
    }
}
