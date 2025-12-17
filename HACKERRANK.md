# HackerRank Deployment Configuration

## Prerequisites
- Java 21
- Maven 3.9+

## Environment Variables
```bash
export PORT=8080
export SPRING_PROFILES_ACTIVE=hackerrank
```

## Build & Run
```bash
# Build
./mvnw clean package -DskipTests

# Run
java -jar target/product-api-0.0.1-SNAPSHOT.jar
```

## Features Enabled
- ✅ REST API (all endpoints)
- ✅ In-memory data (90 products, 4 categories)
- ✅ Pagination (max 50 records)
- ✅ Exception handling (404/500)
- ✅ Async events (analytics/metrics)
- ❌ Redis cache (disabled - not available on HackerRank)
- ❌ Docker (not needed)

## API Endpoints
- `GET /api/v1/products` - List products with pagination
- `GET /api/v1/products/{id}` - Get product by ID
- `GET /api/v1/categories` - List categories
- `GET /api/v1/metrics/products/{id}/views` - Get product views

## Health Check
```bash
curl http://localhost:8080/api/v1/products?size=5
```
