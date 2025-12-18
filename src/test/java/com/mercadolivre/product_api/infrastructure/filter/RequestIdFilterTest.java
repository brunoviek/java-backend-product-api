package com.mercadolivre.product_api.infrastructure.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.MDC;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RequestIdFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private RequestIdFilter filter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        filter = new RequestIdFilter();
        MDC.clear();
    }

    @Test
    void shouldGenerateRequestIdWhenNotPresent() throws ServletException, IOException {
        when(request.getHeader("X-Request-ID")).thenReturn(null);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/v1/products");

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setHeader(eq("X-Request-ID"), anyString());
        verify(filterChain).doFilter(request, response);
        assertNull(MDC.get("requestId"));
    }

    @Test
    void shouldUseExistingRequestIdWhenPresent() throws ServletException, IOException {
        String existingRequestId = "existing-request-id-123";
        when(request.getHeader("X-Request-ID")).thenReturn(existingRequestId);
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/v1/products");

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setHeader("X-Request-ID", existingRequestId);
        verify(filterChain).doFilter(request, response);
        assertNull(MDC.get("requestId"));
    }

    @Test
    void shouldClearMDCAfterRequest() throws ServletException, IOException {
        when(request.getHeader("X-Request-ID")).thenReturn(null);
        when(request.getMethod()).thenReturn("DELETE");
        when(request.getRequestURI()).thenReturn("/api/v1/products/123");

        filter.doFilterInternal(request, response, filterChain);

        assertNull(MDC.get("requestId"));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldHandleExceptionAndClearMDC() throws ServletException, IOException {
        when(request.getHeader("X-Request-ID")).thenReturn(null);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/v1/products");
        doThrow(new ServletException("Test exception")).when(filterChain).doFilter(request, response);

        assertThrows(ServletException.class, () -> 
            filter.doFilterInternal(request, response, filterChain));

        assertNull(MDC.get("requestId"));
    }

    @Test
    void shouldGenerateRequestIdWhenEmpty() throws ServletException, IOException {
        when(request.getHeader("X-Request-ID")).thenReturn("");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/v1/products");

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setHeader(eq("X-Request-ID"), anyString());
        verify(filterChain).doFilter(request, response);
        assertNull(MDC.get("requestId"));
    }
}
