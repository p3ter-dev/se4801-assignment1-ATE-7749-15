# ShopWave-Starter

**SE-4801: Enterprise Application Development: Assignment 1**

**Name:** Peter Kinfe <br/>
**ID:** ATE/7749/15


## What This Project Is

ShopWave-Starter is a Spring Boot 3.x REST API that implements a simplified product catalogue for an e-commerce platform. It demonstrates core enterprise Java concepts including layered architecture, JPA entity relationships, RESTful API design, global exception handling, input validation, pagination, and automated testing.

The application exposes a product management API with five endpoints, backed by an H2 in-memory database for development and testing. The codebase follows a strict four-layer architecture: Controller → Service → Repository → Database.

---

## Prerequisites

Before running the project, make sure you have the following installed on your machine.

**Java 21** is required. You can verify your version by running `java -version` in a terminal. The output should show `openjdk 21` or `java version "21"`. If you need to install it, download it from [https://adoptium.net](https://adoptium.net).

**Maven 3.9+** is required to build the project. Verify with `mvn -version`. If it is not installed, download it from [https://maven.apache.org/download.cgi](https://maven.apache.org/download.cgi) and follow the installation guide for your operating system.

**Git** is needed to clone the repository. Verify with `git --version`.

No external database installation is required — the project uses an H2 in-memory database that starts automatically with the application.

---

## How to Clone the Repository

Open a terminal and run the following command, replacing `<StudentNumber>` with the actual student number:

```bash
git clone https://github.com/p3ter-dev/se4801-assignment1-ATE-7749-15.git
cd se4801-assignment1-ATE-7749-15/shopwave-starter/
```

---

## How to Build the Project

From inside the project root directory (the folder that contains `pom.xml`), run:

```bash
mvn clean install
```

What this command does: `clean` deletes any previously compiled files from the `target/` directory so you start from a clean state. `install` then compiles all source code, runs all tests, packages the application into a JAR file, and installs it in your local Maven repository. If the build succeeds, you will see `BUILD SUCCESS` at the end of the output.

If you want to build without running the tests (faster, useful when you just want the JAR):

```bash
mvn clean package -DskipTests
```

---

## How to Run the Application

### Option 1 — Using the Spring Boot Maven Plugin (recommended for development)

```bash
mvn spring-boot:run
```

This compiles and starts the application in one step. You will see the Spring Boot banner in the console followed by a line like:

```
Started ShopWaveApplication in 2.345 seconds (process running for 3.1)
```

Once you see that line, the application is fully running and ready to accept requests on port 8080.

### Option 2 — Running the compiled JAR directly

First build the JAR (if you have not already):

```bash
mvn clean package -DskipTests
```

Then run it:

```bash
java -jar target/shopwave-starter-0.0.1-SNAPSHOT.jar
```

This is how you would run the application in a production or CI/CD environment.

### Option 3 — Running from IntelliJ IDEA

Open the project in IntelliJ, navigate to `src/main/java/com/shopwave/ShopWaveApplication.java`, and click the green play button next to the `main()` method. IntelliJ will compile and launch the application automatically.

---

## How to Verify the Application is Running

Once started, open a browser or terminal and check the health endpoint provided by Spring Boot Actuator:

```bash
curl http://localhost:8080/actuator/health
```

You should receive `{"status":"UP"}`. This confirms the application is running and connected to the H2 database.

You can also open the H2 database console in your browser at:

```
http://localhost:8080/h2-console
```

Use these connection settings in the console:
- **JDBC URL:** `jdbc:h2:mem:shopwavedb`
- **Username:** `sa`
- **Password:** *(leave blank)*

Once connected, run `SHOW TABLES;` to confirm that Hibernate created the `categories`, `products`, `orders`, and `order_items` tables.

---

## Seed Data

The application includes a `DataSeeder` class that automatically inserts sample data every time the application starts. On startup you will see this message in the console:

```
     Data seeded successfully — 2 categories, 3 products.
```

The seeder creates two categories (Electronics and Clothing) and three products (Laptop Pro 15, Wireless Headphones, and Running Shoes), so you can start testing the API endpoints immediately without manually inserting data.

---

## API Endpoints

The base URL for all endpoints is `http://localhost:8080/api`.

### GET /api/products — Paginated product list

```bash
curl "http://localhost:8080/api/products?page=0&size=10"
```

Returns a `Page<ProductDTO>` object containing the products for the requested page along with pagination metadata (total pages, total elements, etc.).

### GET /api/products/{id} — Single product by ID

```bash
curl "http://localhost:8080/api/products/1"
```

Returns a single `ProductDTO` if the product exists, or a structured `404 Not Found` JSON error if it does not.

### POST /api/products — Create a new product

```bash
curl -X POST "http://localhost:8080/api/products" \
     -H "Content-Type: application/json" \
     -d '{
       "name": "Mechanical Keyboard",
       "description": "Tactile switches, RGB backlight",
       "price": 149.99,
       "stock": 40,
       "categoryId": 1
     }'
```

Returns `201 Created` with the newly created `ProductDTO`. Returns `400 Bad Request` with validation details if the request body violates any constraints (blank name, negative price, etc.).

### GET /api/products/search — Search by keyword and/or max price

```bash
# Search by keyword only
curl "http://localhost:8080/api/products/search?keyword=laptop"

# Filter by max price only
curl "http://localhost:8080/api/products/search?maxPrice=300"

# Combine both filters
curl "http://localhost:8080/api/products/search?keyword=head&maxPrice=300"
```

Both query parameters are optional. Returns `200 OK` with a `List<ProductDTO>`.

### PATCH /api/products/{id}/stock — Update product stock

```bash
# Reduce stock by 5 (sale)
curl -X PATCH "http://localhost:8080/api/products/1/stock" \
     -H "Content-Type: application/json" \
     -d '{ "delta": -5 }'

# Increase stock by 20 (restock)
curl -X PATCH "http://localhost:8080/api/products/1/stock" \
     -H "Content-Type: application/json" \
     -d '{ "delta": 20 }'
```

A positive delta adds stock; a negative delta removes it. Returns `400 Bad Request` if the delta would cause stock to drop below zero. Returns `404 Not Found` if the product ID does not exist.

---

## Error Response Format

All errors follow a consistent JSON structure:

```json
{
  "timestamp": "2024-03-15 10:35:00",
  "status": 404,
  "error": "Not Found",
  "message": "Product not found with id: 999",
  "path": "/api/products/999"
}
```

This is handled globally by `GlobalExceptionHandler.java`, which intercepts `ProductNotFoundException` (404), `MethodArgumentNotValidException` (400), and `IllegalArgumentException` (400).

---

## How to Run the Tests

### Run all tests

```bash
mvn test
```

Maven will compile the test classes, start a minimal Spring context for each test suite, and run all tests. When they all pass you will see:

```
Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### Run a specific test class

```bash
# Run only the service unit tests
mvn test -Dtest=ProductServiceTest

# Run only the controller slice tests
mvn test -Dtest=ProductControllerTest

# Run only the repository integration tests
mvn test -Dtest=ProductRepositoryTest
```

### Understanding the three test types

**ProductServiceTest** is a pure unit test using Mockito. It runs with no Spring context and no database — every dependency is replaced with a mock. This makes it the fastest test class and the one you will run most often during development.

**ProductControllerTest** is a `@WebMvcTest` slice test. It loads only the web layer (controllers, filters, Jackson, validation) and uses MockMvc to fire fake HTTP requests. The service layer is mocked with `@MockBean`. This verifies that URL mappings, status codes, JSON serialisation, and exception handling all work correctly.

**ProductRepositoryTest** is a `@DataJpaTest` integration test. It loads the JPA layer and runs against a real H2 database. Each test runs inside a transaction that is automatically rolled back when the test finishes, so tests never pollute each other's data. This verifies that the derived query methods (`findByNameContainingIgnoreCase`, `findByPriceLessThanEqual`, etc.) generate correct SQL.

---

## Technologies Used

| Technology | Version | Purpose |
|---|---|---|
| Java | 21 | Primary language; uses Records, sealed classes, virtual threads |
| Spring Boot | 3.2.5 | Application framework, auto-configuration, embedded server |
| Spring Web | 3.2.5 | REST controllers, request mapping, Jackson JSON |
| Spring Data JPA | 3.2.5 | Repository layer, derived queries, pagination |
| Hibernate | 6.x | JPA implementation, schema generation, SQL |
| H2 Database | 2.x | In-memory database for development and testing |
| Lombok | 1.18.x | Code generation (@Data, @Builder, @RequiredArgsConstructor) |
| Spring Boot Actuator | 3.2.5 | Health checks, metrics endpoints |
| Spring Boot Validation | 3.2.5 | @Valid, @NotBlank, @Positive, @Min constraints |
| JUnit 5 | 5.x | Test framework |
| Mockito | 5.x | Mocking framework for unit tests |
| AssertJ | 3.x | Fluent assertion library |
