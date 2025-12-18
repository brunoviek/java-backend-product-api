# Health Check Script for Product API (Windows PowerShell)

param(
    [string]$ApiUrl = "http://localhost:8080",
    [int]$Timeout = 5,
    [int]$MaxRetries = 3
)

$ErrorActionPreference = "Stop"

Write-Host "======================================" -ForegroundColor Cyan
Write-Host "  Product API Health Check" -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Cyan
Write-Host ""

function Test-Endpoint {
    param(
        [string]$Endpoint,
        [string]$Description
    )
    
    Write-Host "Checking $Description... " -NoNewline
    
    $retry = 0
    while ($retry -lt $MaxRetries) {
        try {
            $response = Invoke-WebRequest -Uri "$ApiUrl$Endpoint" -Method GET -TimeoutSec $Timeout -ErrorAction Stop
            if ($response.StatusCode -eq 200) {
                Write-Host "✓ OK" -ForegroundColor Green
                return $true
            }
        }
        catch {
            $retry++
            if ($retry -lt $MaxRetries) {
                Start-Sleep -Seconds 2
            }
        }
    }
    
    Write-Host "✗ FAILED" -ForegroundColor Red
    return $false
}

# Check basic health
if (-not (Test-Endpoint "/actuator/health" "Health Endpoint")) {
    Write-Host "API is not responding properly" -ForegroundColor Red
    exit 1
}

# Check API endpoints
Test-Endpoint "/api/v1/products?page=0&size=1" "Products Endpoint" | Out-Null
Test-Endpoint "/api/v1/categories?page=0&size=1" "Categories Endpoint" | Out-Null

# Get API info
Write-Host ""
Write-Host "Getting API information..."
try {
    $info = Invoke-RestMethod -Uri "$ApiUrl/actuator/info" -TimeoutSec $Timeout
    $info | ConvertTo-Json -Depth 5 | Write-Host
}
catch {
    Write-Host "Could not retrieve API info" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "======================================" -ForegroundColor Green
Write-Host "  All checks passed!" -ForegroundColor Green
Write-Host "======================================" -ForegroundColor Green
Write-Host ""
Write-Host "API is healthy and ready to serve requests"
Write-Host "Swagger UI: $ApiUrl/swagger-ui.html"
Write-Host ""

exit 0
