# API Documentation

## Table of Contents

- [Overview](#overview)
- [Authentication](#authentication)
- [Base URL](#base-url)
- [Response Format](#response-format)
- [Error Handling](#error-handling)
- [Endpoints](#endpoints)
- [Rate Limiting](#rate-limiting)
- [Versioning](#versioning)

## Overview

Product API is a RESTful API designed for high-performance e-commerce product catalog management. It provides endpoints for managing products, categories, and images with built-in caching, pagination, and filtering.

### Key Features

- **RESTful Design**: Standard HTTP methods and status codes
- **Pagination**: All list endpoints support pagination
- **Filtering**: Advanced search and filter capabilities
- **Caching**: Redis-based distributed caching
- **Documentation**: Interactive Swagger UI
- **Monitoring**: Health checks and metrics

## Authentication

**Note:** Current version does not require authentication. This is intended for development/demo purposes.

For production deployment, implement one of:
- OAuth 2.0
- JWT tokens
- API keys

## Base URL

```
Local Development: http://localhost:8080
Docker: http://localhost:8080
Production: https://api.yourdomain.com
```

## Response Format

All responses follow a standardized format:

### Success Response

```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": {
    // Response data here
  },
  "timestamp": "2025-12-18T10:30:00Z"
}
```

### Paginated Response

```json
{
  "success": true,
  "message": "Products retrieved successfully",
  "data": {
    "content": [ /* array of items */ ],
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 100,
    "totalPages": 10,
    "first": true,
    "last": false,
    "empty": false
  },
  "timestamp": "2025-12-18T10:30:00Z"
}
```

### Error Response

```json
{
  "success": false,
  "message": "Resource not found",
  "error": "Product with id '123' not found",
  "timestamp": "2025-12-18T10:30:00Z"
}
```

## Error Handling

### HTTP Status Codes

| Code | Description |
|------|-------------|
| 200  | Success |
| 201  | Created |
| 400  | Bad Request - Invalid input |
| 404  | Not Found - Resource doesn't exist |
| 500  | Internal Server Error |

### Common Error Scenarios

#### 404 - Resource Not Found
```json
{
  "success": false,
  "message": "Resource not found",
  "error": "Product with id 'abc123' not found",
  "timestamp": "2025-12-18T10:30:00Z"
}
```

#### 400 - Validation Error
```json
{
  "success": false,
  "message": "Validation failed",
  "error": "Invalid request parameters",
  "timestamp": "2025-12-18T10:30:00Z"
}
```

## Endpoints

### Products

#### List All Products
```http
GET /api/v1/products
```

**Query Parameters:**
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 10)
- `name` (optional): Filter by product name
- `category` (optional): Filter by category

**Example:**
```bash
curl "http://localhost:8080/api/v1/products?page=0&size=10&name=iPhone"
```

#### Get Product by ID
```http
GET /api/v1/products/{id}
```

**Path Parameters:**
- `id`: Product ID (required)

**Example:**
```bash
curl "http://localhost:8080/api/v1/products/abc123"
```

#### Get Products by Category
```http
GET /api/v1/products/category/{category}
```

**Path Parameters:**
- `category`: Category name (required)

**Query Parameters:**
- `page` (optional): Page number
- `size` (optional): Page size

**Example:**
```bash
curl "http://localhost:8080/api/v1/products/category/electronics?page=0&size=20"
```

#### Get Recommended Products
```http
GET /api/v1/products/{id}/recommended
```

**Path Parameters:**
- `id`: Product ID (required)

**Query Parameters:**
- `page` (optional): Page number
- `size` (optional): Page size

### Categories

#### List All Categories
```http
GET /api/v1/categories
```

**Query Parameters:**
- `page` (optional): Page number
- `size` (optional): Page size

#### Get Category by ID
```http
GET /api/v1/categories/{id}
```

#### Get Category by Slug
```http
GET /api/v1/categories/slug/{slug}
```

### Product Images

#### Get Product Images
```http
GET /api/v1/products/{productId}/images
```

**Query Parameters:**
- `page` (optional): Page number
- `size` (optional): Page size

### Metrics

#### Get Product View Metrics
```http
GET /api/v1/metrics/products/{productId}/views
```

**Response:**
```json
{
  "success": true,
  "data": {
    "productId": "abc123",
    "viewCount": 42,
    "note": "Metrics processed asynchronously via Spring Events"
  }
}
```

#### Get Category View Metrics
```http
GET /api/v1/metrics/categories/{category}/views
```

## Rate Limiting

**Not implemented in current version.**

For production, implement rate limiting:
- 100 requests per minute per IP
- 1000 requests per hour per API key
- Burst allowance: 20 requests

## Versioning

Current version: **v1**

API versioning is implemented via URL path:
```
/api/v1/products
/api/v2/products (future)
```

## Interactive Documentation

Access the interactive Swagger UI at:
```
http://localhost:8080/swagger-ui.html
```

## Code Examples

### cURL

```bash
# Get all products
curl -X GET "http://localhost:8080/api/v1/products?page=0&size=10"

# Get product by ID
curl -X GET "http://localhost:8080/api/v1/products/abc123"

# Search products
curl -X GET "http://localhost:8080/api/v1/products?name=iPhone&category=electronics"
```

### JavaScript (fetch)

```javascript
// Get all products
const response = await fetch('http://localhost:8080/api/v1/products?page=0&size=10');
const data = await response.json();
console.log(data);

// Get product by ID
const product = await fetch('http://localhost:8080/api/v1/products/abc123')
  .then(res => res.json());
```

### Python (requests)

```python
import requests

# Get all products
response = requests.get('http://localhost:8080/api/v1/products', 
                       params={'page': 0, 'size': 10})
products = response.json()

# Get product by ID
product = requests.get('http://localhost:8080/api/v1/products/abc123').json()
```

### Java

```java
// Using RestTemplate
RestTemplate restTemplate = new RestTemplate();
String url = "http://localhost:8080/api/v1/products?page=0&size=10";
ApiResponse response = restTemplate.getForObject(url, ApiResponse.class);
```

## Support

- **Documentation**: [README.md](../README.md)
- **Issues**: [GitHub Issues](https://github.com/mercadolivre/product-api/issues)
- **Email**: support@mercadolivre.com
