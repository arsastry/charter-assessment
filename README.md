# Rewards Program - Charter Assessment

## Overview
A Spring Boot REST API that calculates reward points for a retailer's customer loyalty program based on recorded purchase transactions.

### Reward Rules
- **2 points** for every dollar spent **over $100** in each transaction
- **1 point** for every dollar spent **between $50 and $100** in each transaction
- **0 points** for transactions of **$50 or less**

**Example:** A $120 purchase = 2×$20 + 1×$50 = **90 points**

## Tech Stack
- Java 17
- Spring Boot 3.2.5
- Spring Data JPA
- H2 In-Memory Database
- Maven
- JUnit 5 + Mockito (testing)

## Project Structure
```
rewards-program/
├── pom.xml
├── README.md
├── src/
│   ├── main/java/com/charter/rewards/
│   │   ├── RewardsProgramApplication.java    # Spring Boot entry point
│   │   ├── DataLoader.java                   # Loads sample data on startup
│   │   ├── controller/
│   │   │   └── RewardsController.java        # REST endpoints
│   │   ├── service/
│   │   │   └── RewardsService.java           # Business logic & points calculation
│   │   ├── repository/
│   │   │   └── TransactionRepository.java    # Data access layer
│   │   ├── model/
│   │   │   └── Transaction.java              # JPA entity
│   │   ├── dto/
│   │   │   └── CustomerRewardsResponse.java  # Response DTO (Java record)
│   │   └── exception/
│   │       ├── CustomerNotFoundException.java
│   │       └── GlobalExceptionHandler.java   # Centralized error handling
│   ├── main/resources/
│   │   └── application.properties
│   └── test/java/com/charter/rewards/
│       ├── service/
│       │   └── RewardsServiceTest.java               # Unit tests
│       └── controller/
│           └── RewardsControllerIntegrationTest.java  # Integration tests
```

## API Endpoints

| Method | URL                        | Description                              |
|--------|----------------------------|------------------------------------------|
| GET    | `/api/rewards`             | Get reward points for all customers      |
| GET    | `/api/rewards/{customerId}`| Get reward points for a specific customer|

### Sample Response — `GET /api/rewards/1`
```json
{
  "customerId": 1,
  "customerName": "Alice Johnson",
  "monthlyPoints": {
    "2026-03": 115,
    "2026-04": 250,
    "2026-05": 70
  },
  "totalPoints": 435
}
```

## How to Run

### Prerequisites
- Java 17+
- Maven 3.8+

### Build & Run
```bash
cd rewards-program
mvn clean install
mvn spring-boot:run
```

The application starts on `http://localhost:8080`.

### Run Tests
```bash
mvn test
```

### H2 Console
Access the H2 database console at `http://localhost:8080/h2-console` with:
- JDBC URL: `jdbc:h2:mem:rewardsdb`
- Username: `sa`
- Password: *(empty)*

## Sample Data
The application loads sample transaction data dynamically on startup (see `DataLoader.java`). Dates are calculated relative to the current date so they always fall within the last 3 months. The dataset includes:
- **3 customers** (Alice, Bob, Charlie)
- **12 transactions** spanning 3 months
- Various amounts covering all point thresholds ($45, $50, $55, $75, $90, $110, $120, $150, $180, $200, $300)

## Design Decisions
1. **Months are not hardcoded** — the service dynamically calculates the 3-month window from the current date.
2. **Points calculation uses `Math.floor()`** — fractional dollars are truncated before point calculation.
3. **Global exception handler** — provides consistent error responses with proper HTTP status codes.
4. **Java records** — used for the response DTO for immutability and conciseness.

