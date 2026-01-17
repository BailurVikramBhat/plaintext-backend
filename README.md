# Plaintext Backend

## Overview
Plaintext Backend is a scalable, microservices-based architecture designed to power the Plaintext social platform. It provides secure authentication, user management, and core content services including posts and feeds. The system is built using Java and Spring Boot, leveraging a multi-module Maven project structure for modularity and maintainability.

## Architecture
The application is organized as a multi-module Maven project containing the following modules:

*   **plaintext-backend**: The root parent module managing dependencies and build configuration.
*   **plaintext-common**: A shared library containing cross-cutting concerns such as data models (JPA Entities), custom exceptions, global error handling, and utility classes.
*   **plaintext-auth**: The Authentication Service responsible for user registration, login, and JWT token generation. It handles security constraints and user persistence.
*   **plaintext-core**: The Core Service managing the main business domain, including creating posts, retrieving user feeds, and content interactions.

## Technology Stack
*   **Language**: Java 21
*   **Framework**: Spring Boot 3.x / 4.x
*   **Build Tool**: Maven
*   **Database**: PostgreSQL
*   **Security**: Spring Security, JWT (JSON Web Tokens)
*   **ORM**: Hibernate / Spring Data JPA

## Prerequisites
Ensure the following are installed on your local development environment:
*   Java Development Kit (JDK) 21 or higher
*   Maven 3.8+
*   PostgreSQL 14+

## Getting Started

### 1. Database Setup
Create a PostgreSQL database named `plaintext_db` and ensure the credentials match your application configuration (default: user `plaintext_user`, password `plaintext_password`).

### 2. Build the Project
Navigate to the project root and run the following command to build all modules:

```bash
mvn clean install
```

### 3. Running the Services
You can run the individual services using the Spring Boot Maven plugin or by executing the generated JAR files.

#### Running Authentication Service
```bash
mvn spring-boot:run -pl plaintext-auth
```
Service runs on port: `8081`

#### Running Core Service
```bash
mvn spring-boot:run -pl plaintext-core
```
Service runs on port: `8082`

## API Endpoints

### Authentication (Port 8081)
*   **POST** `/api/auth/signup` - Register a new user account.
*   **POST** `/api/auth/login` - Authenticate and receive a JWT.

### Core (Port 8082)
*   **POST** `/api/posts` - Create a new post (Requires Authentication).
*   **GET** `/api/feed` - Retrieve the global post feed.
*   **GET** `/api/posts/user/{username}` - Retrieve posts for a specific user.
*   **GET** `/actuator/health` - Check service health status.

## Error Handling
The application uses a centralized error handling mechanism. Errors are returned in a standard JSON format:
```json
{
  "timestamp": "2023-10-27T10:00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource",
  "path": "/api/posts"
}
```
All services are configured to return consistent error responses for both business exceptions (e.g., 409 Conflict) and security exceptions (e.g., 401 Unauthorized).
