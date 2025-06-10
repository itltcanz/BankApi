# Система управления банковскими картами

This project is a test assignment.

## Table of Contents

- [Installation and Launch](#installation-and-launch)
- [Usage](#usage)

## Installation and Launch

### Prerequisites

Before starting, ensure the following tools are installed:
- [Docker](https://docs.docker.com/get-docker/)
- [Docker Compose](https://docs.docker.com/compose/install/)
- [JDK 17](https://adoptium.net/temurin/releases/)
- [Maven](https://maven.apache.org/download.cgi)

### Installation and launch

1. Clone the repository:

    ```bash
    git clone https://github.com/itltcanz/BankApi.git
    cd BankApi
    ```

2. Create `.env` based on `.env-example`:
   ```plaintext
   SPRING_DATASOURCE_USERNAME=postgres
   SPRING_DATASOURCE_PASSWORD=pass
   SPRING_DATA_REDIS_HOST=localhost
   SPRING_DATA_REDIS_PORT=6379
   JWT_SECRET=53A73E5F1C4E0A2D3B5F2D784E6A1B4274OMH4TY1F6E5C3A596D635A75327855

3. Start the containers:

    ```bash
    docker compose up -d
    ```

   Installation and launch is now complete.

## Usage

### Swagger

A full description of the API endpoints is available in Swagger UI at:

   ```url
   http://localhost:8080/swagger-ui/index.html
   ```
