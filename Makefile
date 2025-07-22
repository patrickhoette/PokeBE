# Variables
PROJECT_NAME = pokebe

API_DIST_DIR = ./api/build/install/api

INGEST_INPUT = $(wildcard ingest/csv/*.csv)
INGEST_OUTPUT = ./build/data-ingested.flag

# Colours
BLACK = \033[1;30m
RED = \033[1;31m
GREEN = \033[1;32m
YELLOW = \033[1;33m
BLUE = \033[1;34m
MAGENTA = \033[1;35m
CYAN = \033[1;36m
WHITE = \033[1;37m
RESET = \033[0m

# Targets
.DEFAULT_GOAL := help
.PHONY: help status rebuild-api rebuild-api-nocache restart-api build run up down clean ingest logs setup restart-sprite rebuild-sprite restart-db rebuild-db

help:
	@echo ""
	@echo "$(WHITE)Usage make <target>$(RESET)"
	@echo ""
	@echo "$(GREEN)Targets:$(RESET)"
	@grep -E '^[a-zA-Z_-]+:.*?## ' Makefile | \
		awk 'BEGIN {FS = ":.*?## "}; {printf "  $(BLUE)%-22s$(RESET)| %s\n", $$1, $$2}'

# Commands
status: ## Show status of all Docker services
	@docker-compose ps

rebuild-api: build ## Rebuild and rerun only the API
	@echo "$(WHITE)=> 🔄 Rebuilding API$(RESET)"
	@echo "$(BLUE)  -> Building Docker Compose...$(RESET)"
	@docker-compose build api
	@echo "$(BLUE)  -> Starting API...$(RESET)"
	@docker-compose up -d api
	@echo "$(GREEN)=> Rebuilding API done!$(RESET)"

rebuild-api-nocache: build ## Full rebuild of the API without cache
	@echo "$(WHITE)=> 🔁 Rebuilding API without cache$(RESET)"
	@echo "$(BLUE)  -> Building Docker Compose without cache...$(RESET)"
	@docker-compose build --no-cache api
	@echo "$(BLUE)  -> Starting API...$(RESET)"
	@docker-compose up -d api
	@echo "$(GREEN)=> Rebuilding API done!$(RESET)"

restart-api: ## Restart API container without rebuilding
	@echo "$(WHITE)=> ♻️ Restarting API"
	@echo "$(BLUE)  -> Stopping API...$(RESET)"
	@docker-compose stop api
	@echo "$(BLUE)  -> Removing API...$(RESET)"
	@docker-compose rm -f api
	@echo "$(BLUE)  -> Starting API...$(RESET)"
	@docker-compose up -d api
	@echo "$(GREEN)=> Restarting API done!$(RESET)"

build: ## Build the API
	@echo "$(WHITE)=> 🛠️ Building API$(RESET)"
	@./gradlew --console=plain :api:installDist
	@echo "$(GREEN)=> Building API done!$(RESET)"

run: ## Run API with docker-compose without rebuilding
	@echo "$(WHITE)=> ▶️ Running Docker Compose$(RESET)"
	@docker-compose up

up: build $(INGEST_OUTPUT) ## Build and start all services
	@echo "$(WHITE)=> 🚀 Building up Docker Compose$(RESET)"
	@docker-compose up --build

down: ## Stop and remove containers
	@echo "$(WHITE)=> 🧯 Taking down Docker Compose$(RESET)"
	@docker-compose down
	@echo "$(GREEN)=> Taking down Docker Compose complete!$(RESET)"

clean: ## Clean build and docker artifacts
	@echo "$(WHITE)=> 🧹 Cleaning$(RESET)"
	@echo "$(BLUE)  -> Cleaning Gradle...$(RESET)"
	@./gradlew --console=plain clean
	@echo "$(BLUE)  -> Taking down Docker Compose...$(RESET)"
	@docker-compose down -v
	@echo "$(BLUE)  -> Pruning Docker system...$(RESET)"
	@docker system prune -f
	@echo "$(GREEN)=> Cleaning complete!$(RESET)"

ingest: ## Run Python ingestor service
	@echo "$(WHITE)=> 🧪 Force Ingesting$(RESET)"
	@echo "$(BLUE)  -> Force re-ingesting CSVs...$(RESET)"
	@docker-compose up --build --abort-on-container-exit --exit-code-from ingest ingest
	@touch $(INGEST_OUTPUT)
	@echo "$(GREEN)=> Force Ingesting complete!$(RESET)"

logs: ## Tail logs for the API service
	@docker-compose logs -f api

setup: fetch-csv ## Setup repository for development

fetch-csv: ## Fetch the PokeAPI CSVs
	@echo "$(WHITE)=> 📦 Fetching PokeAPI CSVs$(RESET)"
	@echo "$(BLUE)  -> Creating tmp dir$(RESET)"
	@mkdir -p build/tmp-pokeapi
	@echo "$(BLUE)  -> Cloning repo$(RESET)"
	@git clone --depth 1 --filter=blob:none --sparse https://github.com/PokeAPI/pokeapi build/tmp-pokeapi
	@echo "$(BLUE)  -> Cloning CSVs$(RESET)"
	@cd build/tmp-pokeapi && git sparse-checkout set data/v2/csv
	@cd ../../
	@mkdir -p ingest/csv
	@echo "$(BLUE)  -> Moving CSVs$(RESET)"
	@cp -r build/tmp-pokeapi/data/v2/csv/* ingest/csv/
	@echo "$(BLUE)  -> Deleting tmp dir$(RESET)"
	@rm -rf build/tmp-pokeapi
	@echo "$(GREEN)=>Fetching PokeAPI CSVs Complete$(RESET)"

$(INGEST_OUTPUT): $(INGEST_INPUT)
	@echo "$(WHITE)=> 🧪 Ingesting$(RESET)"
	@echo "$(BLUE)  -> CSVs changed. Running ingestion...$(RESET)"
	@docker-compose up --build --abort-on-container-exit --exit-code-from ingest ingest
	@touch $(INGEST_OUTPUT)
	@echo "$(GREEN)=> Ingesting complete!$(RESET)"

restart-sprite: ## Restart the sprite server
	@echo "$(WHITE)=> 🖼️ Restarting Sprite Server$(RESET)"
	@echo "$(BLUE)  -> Stopping Sprite Server...$(RESET)"
	@docker-compose stop sprite-server
	@echo "$(BLUE)  -> Removing Sprite Server...$(RESET)"
	@docker-compose rm -f -v sprite-server
	@echo "$(BLUE)  -> Starting Sprite Server...$(RESET)"
	@docker-compose up -d sprite-server
	@echo "$(GREEN)=> Restarting Sprite Server done!$(RESET)"

rebuild-sprite: ## Rebuild, ingest, and run the sprite server
	@echo "$(WHITE)=> 🧱 Rebuilding Sprite Server and Ingest$(RESET)"
	@echo "$(BLUE)  -> Stopping Sprite Server...$(RESET)"
	@docker-compose stop sprite-server
	@echo "$(BLUE)  -> Removing Sprite Server...$(RESET)"
	@docker-compose rm -f -v sprite-server
	@echo "$(BLUE)  -> Removing poke-sprites volume...$(RESET)"
	@docker volume rm -f poke-sprites
	@echo "$(BLUE)  -> Re-running ingestion...$(RESET)"
	@make ingest
	@echo "$(BLUE)  -> Starting Sprite Server...$(RESET)"
	@docker-compose up -d sprite-server
	@echo "$(GREEN)=> Sprite Server rebuild complete!$(RESET)"

restart-db: ## Restart the database
	@echo "$(WHITE)=> 🧬 Restarting Database$(RESET)"
	@echo "$(BLUE)  -> Stopping Database...$(RESET)"
	@docker-compose stop db
	@echo "$(BLUE)  -> Removing Database...$(RESET)"
	@docker-compose rm -f -v db
	@echo "$(BLUE)  -> Starting Database...$(RESET)"
	@docker-compose up -d db
	@echo "$(GREEN)=> Database restarted successfully!$(RESET)"

rebuild-db: ## Rebuild and run the database
	@echo "$(WHITE)=> 🔨 Rebuilding Database from scratch$(RESET)"
	@echo "$(BLUE)  -> Stopping Database...$(RESET)"
	@docker-compose stop db
	@echo "$(BLUE)  -> Removing Database...$(RESET)"
	@docker-compose rm -f -v db
	@echo "$(BLUE)  -> Removing poke-data volume...$(RESET)"
	@docker volume rm -f poke-data
	@echo "$(BLUE)  -> Starting fresh Database...$(RESET)"
	@docker-compose up -d db
	@echo "$(GREEN)=> Database rebuild complete!$(RESET)"
