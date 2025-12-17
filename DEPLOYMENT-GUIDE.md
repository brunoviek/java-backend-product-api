# 🚀 Deployment Guide

Este guia explica como a aplicação funciona em diferentes ambientes usando Spring Profiles.

---

## 📋 Spring Profiles Strategy

### Profile: **default** (sem profile ativo)
```properties
# application.properties (base)
server.port=${PORT:8080}
spring.cache.type=none
```

**Quando usar:**
- Desenvolvimento local rápido (sem Redis)
- Primeira execução para testar
- Ambientes que não suportam Redis

**Como ativar:**
```bash
java -jar target/product-api-0.0.1-SNAPSHOT.jar
```

---

### Profile: **docker**
```properties
# application-docker.properties
server.port=8080
spring.cache.type=redis
spring.data.redis.host=redis
spring.data.redis.port=6379

# Circuit Breaker enabled
resilience4j.circuitbreaker.configs.default.register-health-indicator=true
```

**Quando usar:**
- Desenvolvimento com Docker Compose
- Produção com múltiplas instâncias
- Ambientes que possuem Redis

**Como ativar:**
```bash
# Automaticamente ativado pelo docker-compose.yml
docker-compose up --build

# Ou manualmente
java -Dspring.profiles.active=docker -jar target/product-api-0.0.1-SNAPSHOT.jar
```

**Ativação automática:**
```yaml
# docker-compose.yml e docker-compose-scale.yml
environment:
  - SPRING_PROFILES_ACTIVE=docker
```

---

### Profile: **hackerrank**
```properties
# application-hackerrank.properties
server.port=${PORT:8080}
spring.cache.type=none

# Simplified async config
spring.task.execution.pool.core-size=5
spring.task.execution.pool.max-size=10

# Circuit breaker disabled
management.health.circuitbreakers.enabled=false

# Debug logging
logging.level.com.mercadolivre.product_api=DEBUG
```

**Quando usar:**
- HackerRank platform
- Heroku, Railway, Render
- Qualquer cloud sem Redis

**Como ativar:**
```bash
# Procfile (automático no HackerRank/Heroku)
web: java -Dserver.port=$PORT -Dspring.profiles.active=hackerrank -jar target/product-api-0.0.1-SNAPSHOT.jar

# Ou manualmente
export PORT=3000
java -Dspring.profiles.active=hackerrank -jar target/product-api-0.0.1-SNAPSHOT.jar
```

---

## ✅ Feature Matrix

| Feature | default | docker | hackerrank |
|---------|---------|--------|------------|
| REST API | ✅ | ✅ | ✅ |
| Pagination | ✅ | ✅ | ✅ |
| Filtering | ✅ | ✅ | ✅ |
| ApiResponse<T> | ✅ | ✅ | ✅ |
| Async Events | ✅ | ✅ | ✅ |
| Mock Data | ✅ | ✅ | ✅ |
| **Redis Cache** | ❌ | ✅ | ❌ |
| **Circuit Breaker** | ❌ | ✅ | ❌ |
| **Health Checks** | Basic | Full | Basic |
| **Port Config** | 8080 | 8080 | $PORT |

---

## 🔧 Configuration Hierarchy

Spring Boot carrega as configurações nesta ordem:

```
1. application.properties (base - sempre carregado)
   ↓
2. application-{profile}.properties (sobrescreve o base)
   ↓
3. Environment Variables (sobrescreve tudo)
```

### Exemplo: Docker Profile

**Carregamento:**
```
application.properties
  └─ spring.cache.type=none (base)
     
application-docker.properties  
  └─ spring.cache.type=redis (sobrescreve!)
     
Resultado final: Redis ATIVADO ✅
```

### Exemplo: HackerRank Profile

**Carregamento:**
```
application.properties
  └─ spring.cache.type=none (base)
     └─ server.port=${PORT:8080}
     
application-hackerrank.properties
  └─ spring.cache.type=none (confirma)
     └─ server.port=${PORT:8080}
     
Environment Variable: PORT=3000
     
Resultado final: Cache DESATIVADO ✅, Porta 3000 ✅
```

---

## 🐳 Docker Deployment

### Single Instance

```bash
# docker-compose.yml ativa automaticamente o profile "docker"
docker-compose up --build
```

**Verificação:**
```bash
# Logs devem mostrar
INFO  --- [main] c.m.p.ProductApiApplication: The following 1 profile is active: "docker"
INFO  --- [main] o.s.b.a.c.CacheAutoConfiguration: Cache type: 'redis'
```

### Scaled with Nginx (3 instances)

```bash
# docker-compose-scale.yml também ativa o profile "docker"
./start.ps1
```

**Todas as 3 instâncias compartilham o mesmo Redis!**

---

## ☁️ HackerRank / Cloud Deployment

### Build

```bash
./mvnw clean package -DskipTests
```

### Deploy

O `Procfile` ativa automaticamente:

```procfile
web: java -Dserver.port=$PORT -Dspring.profiles.active=hackerrank -jar target/product-api-0.0.1-SNAPSHOT.jar
```

**HackerRank define automaticamente:**
- `$PORT` (variável de ambiente)
- Profile `hackerrank` (sem Redis)

---

## 🧪 Testing Profiles

Para garantir que funciona em ambos os ambientes:

### Test 1: Docker with Redis

```bash
docker-compose up
curl http://localhost:8080/api/v1/products
curl http://localhost:8080/actuator/health

# Deve mostrar Redis UP
```

### Test 2: HackerRank without Redis

```bash
export SPRING_PROFILES_ACTIVE=hackerrank
java -jar target/product-api-0.0.1-SNAPSHOT.jar

curl http://localhost:8080/api/v1/products
curl http://localhost:8080/actuator/health

# Não deve mencionar Redis (cache desabilitado)
```

---

## 🎯 Quick Reference

**Docker deployment:**
```bash
docker-compose up --build
# Profile: docker
# Redis: ENABLED
# Port: 8080
```

**HackerRank deployment:**
```bash
./mvnw package && java -jar target/*.jar
# Profile: default or hackerrank
# Redis: DISABLED
# Port: $PORT or 8080
```

**Scaled deployment:**
```bash
./start.ps1
# Profile: docker
# Redis: ENABLED (shared)
# Instances: 3
# Load Balancer: Nginx
```

---

## 📝 Summary

- ✅ Docker continua funcionando **EXATAMENTE** como antes
- ✅ Profile `docker` ativa automaticamente via docker-compose
- ✅ Redis e Circuit Breaker funcionam no Docker
- ✅ HackerRank roda sem dependências externas
- ✅ Aplicação funciona nos dois ambientes **simultaneamente**

**Nenhuma mudança quebrou o funcionamento existente!** 🎉
