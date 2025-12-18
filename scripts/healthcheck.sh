#!/bin/bash
# Health Check Script for Product API

set -e

API_URL="${API_URL:-http://localhost:8080}"
TIMEOUT="${TIMEOUT:-5}"
MAX_RETRIES="${MAX_RETRIES:-3}"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "======================================"
echo "  Product API Health Check"
echo "======================================"
echo ""

# Function to check endpoint
check_endpoint() {
    local endpoint=$1
    local description=$2
    local retry=0
    
    echo -n "Checking $description... "
    
    while [ $retry -lt $MAX_RETRIES ]; do
        if curl -sf --max-time $TIMEOUT "$API_URL$endpoint" > /dev/null 2>&1; then
            echo -e "${GREEN}✓ OK${NC}"
            return 0
        fi
        retry=$((retry + 1))
        if [ $retry -lt $MAX_RETRIES ]; then
            sleep 2
        fi
    done
    
    echo -e "${RED}✗ FAILED${NC}"
    return 1
}

# Check basic health
if ! check_endpoint "/actuator/health" "Health Endpoint"; then
    echo -e "${RED}API is not responding properly${NC}"
    exit 1
fi

# Check API endpoints
check_endpoint "/api/v1/products?page=0&size=1" "Products Endpoint"
check_endpoint "/api/v1/categories?page=0&size=1" "Categories Endpoint"

# Get API info
echo ""
echo "Getting API information..."
if response=$(curl -sf --max-time $TIMEOUT "$API_URL/actuator/info" 2>/dev/null); then
    echo "$response" | python3 -m json.tool 2>/dev/null || echo "$response"
fi

echo ""
echo -e "${GREEN}======================================"
echo "  All checks passed!"
echo "======================================${NC}"
echo ""
echo "API is healthy and ready to serve requests"
echo "Swagger UI: $API_URL/swagger-ui.html"
echo ""

exit 0
