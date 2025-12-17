#!/usr/bin/env pwsh
# Product API - Docker Compose Startup Script
# No Java installation required - Everything runs in Docker!

Write-Host "'n🐳 PRODUCT API - DOCKER DEPLOYMENT'n" -ForegroundColor Cyan

# Check if Docker is running
try {
    docker version | Out-Null
    Write-Host "✓ Docker está rodando'n" -ForegroundColor Green
} catch {
    Write-Host "❌ Docker não está rodando. Inicie o Docker Desktop primeiro.'n" -ForegroundColor Red
    exit 1
}

# Stop existing containers
Write-Host "🧹 Limpando containers antigos..." -ForegroundColor Yellow
docker-compose -f docker-compose-scale.yml down --remove-orphans

# Start with multiple instances
Write-Host "'n🚀 Iniciando aplicação com:" -ForegroundColor Cyan
Write-Host "   • 3 instâncias da aplicação (Spring Boot)" -ForegroundColor White
Write-Host "   • 1 instância do Redis (cache)" -ForegroundColor White
Write-Host "   • 1 instância do Nginx (load balancer)'n" -ForegroundColor White

docker-compose -f docker-compose-scale.yml up --build --scale app=3 -d

# Wait for services to be ready
Write-Host "⏳ Aguardando serviços iniciarem..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

# Check if services are running
Write-Host "'n✅ Verificando serviços:" -ForegroundColor Green
docker-compose -f docker-compose-scale.yml ps

Write-Host "'n🎉 APLICAÇÃO PRONTA!" -ForegroundColor Green
Write-Host "   📍 API: http://localhost" -ForegroundColor Cyan
Write-Host "   📍 Exemplo: http://localhost/api/v1/products'n" -ForegroundColor Cyan

Write-Host "💡 Comandos úteis:" -ForegroundColor Yellow
Write-Host "   Ver logs: docker-compose -f docker-compose-scale.yml logs -f" -ForegroundColor Gray
Write-Host "   Parar: docker-compose -f docker-compose-scale.yml down'n" -ForegroundColor Gray
