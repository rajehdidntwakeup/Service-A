# Service-A - Picture Inventory Management System

A modern Spring Boot microservice for managing picture inventories with REST API.

## Overview

Service-A is a microservice that provides a complete CRUD API for managing picture inventories. The system enables
creating, retrieving, updating, and managing pictures with details such as name, stock, price, and description.

## Technology Stack

- **Java 24** - Latest Java version
- **Spring Boot 3.5.5** - Microservices framework
- **Spring Data JPA** - Data persistence
- **Spring Security** - Authentication and authorization
- **Spring WebFlux** - Reactive programming
- **H2 Database** - In-memory database for development
- **PostgreSQL** - Production database
- **Maven** - Build management
- **JUnit 5** - Testing framework
- **MockMvc** - Web layer testing

## Architecture

The project follows a clean layered architecture:

```
├── Controller Layer    - REST API endpoints
├── Service Layer      - Business logic
├── Repository Layer   - Data access
└── Domain Layer       - Entities and DTOs
```

## Project Structure

```
src/
├── main/java/test/servicea/
│   ├── config/           # Configuration classes
│   ├── controller/       # REST controllers
│   ├── domain/          # Entities and DTOs
│   ├── repository/      # Data access
│   ├── service/         # Business logic
│   └── ServiceAApplication.java
├── main/resources/
│   └── application.properties
└── test/
    ├── java/            # Test classes
    └── resources/
        └── application-test.properties
```

## Installation and Setup

### Prerequisites

- Java 24 or higher
- Maven 3.6 or higher
- PostgreSQL (for production)

### Local Development

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd service-A
   ```

2. **Install dependencies:**
   ```bash
   mvn clean install
   ```

3. **Start the application:**
   ```bash
   mvn spring-boot:run
   ```

4. **Test the application:**
    - API is available at: `http://localhost:8080`
    - H2 Console: `http://localhost:8080/h2-console`
    - JDBC URL: `jdbc:h2:mem:testdb-service-A`
    - Username: `sa`
    - Password: (empty)

## API Documentation

### Base URL

```
http://localhost:8080/api/inventory
```

### Endpoints

| Method | Endpoint              | Description        |
|--------|-----------------------|--------------------|
| `POST` | `/api/inventory`      | Create new picture |
| `GET`  | `/api/inventory`      | Get all pictures   |
| `GET`  | `/api/inventory/{id}` | Get picture by ID  |
| `PUT`  | `/api/inventory/{id}` | Update picture     |

### Example Requests

#### Create Picture

```bash
curl -X POST http://localhost:8080/api/inventory \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Mona Lisa",
    "stock": 1,
    "price": 100.0,
    "description": "Famous painting by Leonardo da Vinci"
  }'
```

#### Get All Pictures

```bash
curl -X GET http://localhost:8080/api/inventory
```

#### Get Picture by ID

```bash
curl -X GET http://localhost:8080/api/inventory/1
```

#### Update Picture

```bash
curl -X PUT http://localhost:8080/api/inventory/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Mona Lisa - Updated",
    "stock": 2,
    "price": 150.0,
    "description": "Updated description"
  }'
```

### Data Model

#### Picture Entity

```json
{
  "id": 1,
  "name": "Picture Name",
  "stock": 10,
  "price": 99.99,
  "description": "Picture description"
}
```

#### PictureDto

```json
{
  "name": "Picture Name",
  "stock": 10,
  "price": 99.99,
  "description": "Picture description"
}
```

### Validation Rules

- **name**: Required field, cannot be empty
- **stock**: Must be >= 0
- **price**: Must be >= 0
- **description**: Required field, cannot be empty

## Testing

### Running Tests

```bash
# All tests
mvn test

# Unit tests only
mvn test -Dtest="*UnitTest"

# Integration tests only
mvn test -Dtest="*IntegrationTest"
```

### Test Coverage

The project contains comprehensive tests:

- **Unit Tests** for Controller and Service layers
- **Integration Tests** with MockMvc
- **Edge Cases** and error handling
- **Validation tests**

## Code Quality

The project uses several code quality tools:

### Checkstyle

```bash
mvn checkstyle:check
```

### PMD

```bash
mvn pmd:check
```

### SpotBugs

```bash
mvn spotbugs:check
```

## Deployment

### Docker (Recommended)

```dockerfile
FROM openjdk:24-jdk-slim
COPY target/service-A-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Production Configuration

Configure PostgreSQL for production:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/servicea_db
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

## Configuration

### Environment Variables

- `DB_USERNAME` - Database username
- `DB_PASSWORD` - Database password
- `DB_URL` - Database URL

### Application Profiles

- `dev` - Development (H2 Database)
- `test` - Testing (H2 In-Memory)
- `prod` - Production (PostgreSQL)

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## Changelog

### Version 0.0.1-SNAPSHOT

- Initial implementation
- CRUD operations for pictures
- REST API with validation
- Comprehensive test coverage
- Code quality tools integrated

## Known Issues

No known issues in the current version.

## Roadmap

- [ ] OpenAPI/Swagger documentation
- [ ] Docker containerization
- [ ] Health checks and monitoring
- [ ] Logging integration
- [ ] API versioning
- [ ] Caching strategies

## Support

For questions or issues:

- Create an issue in the repository
- Contact the developers: Jeremy Heißenberger, Rajeh Abdulhadi, Felix Eibl

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.

---

**Developed by Jeremy Heißenberger, Rajeh Abdulhadi, and Felix Eibl**
