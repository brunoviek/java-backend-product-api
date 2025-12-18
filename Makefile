.PHONY: help build test coverage run docker docker-up docker-down clean lint format

# Colors for output
BLUE := \033[0;34m
GREEN := \033[0;32m
YELLOW := \033[0;33m
NC := \033[0m # No Color

help: ## Show this help message
	@echo "$(BLUE)Product API - Available Commands$(NC)"
	@echo ""
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "  $(GREEN)%-20s$(NC) %s\n", $$1, $$2}'

build: ## Build the project
	@echo "$(BLUE)Building project...$(NC)"
	./mvnw clean install -DskipTests
	@echo "$(GREEN)✓ Build complete$(NC)"

test: ## Run all tests
	@echo "$(BLUE)Running tests...$(NC)"
	./mvnw test
	@echo "$(GREEN)✓ Tests complete$(NC)"

coverage: ## Generate test coverage report
	@echo "$(BLUE)Generating coverage report...$(NC)"
	./mvnw clean test jacoco:report
	@echo "$(GREEN)✓ Coverage report generated at target/site/jacoco/index.html$(NC)"

coverage-check: ## Check coverage thresholds
	@echo "$(BLUE)Checking coverage thresholds...$(NC)"
	./mvnw jacoco:check
	@echo "$(GREEN)✓ Coverage check passed$(NC)"

run: ## Run the application locally
	@echo "$(BLUE)Starting application...$(NC)"
	./mvnw spring-boot:run

run-docker: ## Run with Docker profile
	@echo "$(BLUE)Starting application with Docker profile...$(NC)"
	./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=docker"

docker: ## Build Docker image
	@echo "$(BLUE)Building Docker image...$(NC)"
	docker build -t product-api:latest .
	@echo "$(GREEN)✓ Docker image built$(NC)"

docker-up: ## Start all services with Docker Compose
	@echo "$(BLUE)Starting services with Docker Compose...$(NC)"
	docker-compose up -d
	@echo "$(GREEN)✓ Services started$(NC)"
	@echo "$(YELLOW)API available at: http://localhost:8080$(NC)"
	@echo "$(YELLOW)Swagger UI: http://localhost:8080/swagger-ui.html$(NC)"

docker-up-scale: ## Start services with scaling
	@echo "$(BLUE)Starting scaled services...$(NC)"
	docker-compose -f docker-compose-scale.yml up -d
	@echo "$(GREEN)✓ Scaled services started$(NC)"

docker-down: ## Stop all Docker services
	@echo "$(BLUE)Stopping Docker services...$(NC)"
	docker-compose down
	docker-compose -f docker-compose-scale.yml down
	@echo "$(GREEN)✓ Services stopped$(NC)"

docker-logs: ## Show Docker logs
	docker-compose logs -f

clean: ## Clean build artifacts
	@echo "$(BLUE)Cleaning build artifacts...$(NC)"
	./mvnw clean
	rm -rf target/
	@echo "$(GREEN)✓ Clean complete$(NC)"

deps: ## Display dependency updates
	@echo "$(BLUE)Checking for dependency updates...$(NC)"
	./mvnw versions:display-dependency-updates

deps-update: ## Update all dependencies
	@echo "$(BLUE)Updating dependencies...$(NC)"
	./mvnw versions:use-latest-versions
	@echo "$(GREEN)✓ Dependencies updated$(NC)"

verify: ## Run full verification (build, test, coverage)
	@echo "$(BLUE)Running full verification...$(NC)"
	./mvnw clean verify
	@echo "$(GREEN)✓ Verification complete$(NC)"

package: ## Package the application
	@echo "$(BLUE)Packaging application...$(NC)"
	./mvnw clean package
	@echo "$(GREEN)✓ Package created at target/product-api-1.0.0.jar$(NC)"

install: ## Install to local Maven repository
	@echo "$(BLUE)Installing to local repository...$(NC)"
	./mvnw clean install
	@echo "$(GREEN)✓ Installed$(NC)"

quality: ## Run code quality checks
	@echo "$(BLUE)Running quality checks...$(NC)"
	./mvnw clean verify sonar:sonar
	@echo "$(GREEN)✓ Quality checks complete$(NC)"

security: ## Run security checks
	@echo "$(BLUE)Running security checks...$(NC)"
	./mvnw org.owasp:dependency-check-maven:check
	@echo "$(GREEN)✓ Security check complete$(NC)"

format: ## Format code
	@echo "$(BLUE)Formatting code...$(NC)"
	./mvnw com.spotify.fmt:fmt-maven-plugin:format
	@echo "$(GREEN)✓ Code formatted$(NC)"

init: ## Initialize development environment
	@echo "$(BLUE)Initializing development environment...$(NC)"
	@cp .env.example .env
	./mvnw clean install -DskipTests
	@echo "$(GREEN)✓ Environment initialized$(NC)"
	@echo "$(YELLOW)Don't forget to configure your .env file$(NC)"

all: clean build test ## Clean, build and test

.DEFAULT_GOAL := help
