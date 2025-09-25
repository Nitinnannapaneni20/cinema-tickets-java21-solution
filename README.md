# Cinema Ticket Booking System

This is my implementation of a cinema ticket booking system for a coding challenge. It handles ticket purchases with proper validation, payment processing, and seat reservations using a clean modular architecture.

## Business Rules

- Adults cost £25, Children cost £15, Infants are free
- Maximum 25 tickets can be purchased at once
- Children and infants must be accompanied by at least one adult
- Infants sit on adult laps (no separate seat allocation)
- Only valid account IDs (greater than 0) can purchase tickets

## Prerequisites

You'll need Java 21 and Maven installed. Here's how to check:

```bash
# Check Java version (must be 21)
java -version

# Check Maven is installed
mvn -version
```

If you don't have Java 21, download it from [Oracle](https://www.oracle.com/java/technologies/downloads/) or use [SDKMAN](https://sdkman.io/).

## Getting Started

### 1. Clone and Setup
```bash
git clone <repository-url>
cd cinema-tickets-java21-solution
```

### 2. Install Dependencies
```bash
mvn clean compile
```

### 3. Run Tests

```bash
# Run all tests (48 total: 30 unit + 18 integration)
mvn test

# Run only unit tests
mvn test -Dtest="*Test"

# Run only integration tests
mvn test -Dtest="*IntegrationTest"

# Generate test coverage report
mvn clean test jacoco:report
```

### 4. View Coverage Report
After running `mvn clean test jacoco:report`, open:
```
target/site/jacoco/index.html
```
Currently achieving **95% code coverage**!

### 5. Docker Support

```bash
# Build Docker image
docker build -t cinema-tickets .

# Run tests in Docker
docker run --rm cinema-tickets
```

## How I Built It

I designed this with a modular architecture that separates concerns:

- **TicketServiceImpl**: Main orchestrator that coordinates validation, calculation, and external service calls
- **TicketValidator**: Handles all business rule validation (account validity, ticket limits, adult supervision)
- **CostCalculator**: Calculates total cost using a pricing map
- **SeatCalculator**: Determines seat requirements (adults + children only)

Key design decisions:
- **Single traversal optimization**: Process ticket requests once using Map<TicketType, Count> instead of multiple iterations
- **Validation first**: All business rules checked before calling external services
- **Seats before payment**: Reserve seats first, then charge (better user experience)
- **Functional interfaces**: Modern Java approach with clean separation
- **Comprehensive error handling**: Clear exception messages for all failure scenarios

## SOLID Principles Applied

- **Single Responsibility**: Each component has one clear purpose (TicketValidator for validation, CostCalculator for pricing)
- **Open/Closed**: Functional interfaces allow extending behavior without modifying existing code
- **Interface Segregation**: Small, focused interfaces (TicketValidator, CostCalculator, SeatCalculator)
- **Dependency Inversion**: TicketServiceImpl depends on abstractions, not concrete implementations

## What I Tested

**48 comprehensive test cases** covering:

**Unit Tests (30 tests):**
- TicketServiceImpl orchestration logic with mocked dependencies
- TicketValidator business rule validation
- CostCalculator pricing calculations
- SeatCalculator seat requirements

**Integration Tests (18 tests):**
- End-to-end scenarios with real component interactions
- Family booking scenarios (parents with kids and babies)
- Boundary conditions (exactly 25 tickets)
- Error cases (invalid accounts, business rule violations)
- Edge cases (large families, complex ticket combinations)

## Project Structure

```
src/main/java/uk/gov/dwp/uc/pairtest/
├── TicketService.java              # Interface (unchanged)
├── TicketServiceImpl.java          # Main service implementation
├── calculation/
│   ├── CostCalculator.java         # Pricing calculation interface
│   ├── CostCalculatorImpl.java     # Pricing implementation
│   ├── SeatCalculator.java         # Seat calculation interface
│   └── SeatCalculatorImpl.java     # Seat implementation
├── validation/
│   ├── TicketValidator.java        # Validation interface
│   └── TicketValidatorImpl.java    # Business rule validation
├── domain/
│   └── TicketTypeRequest.java      # Domain model (unchanged)
└── exception/
    ├── InvalidPurchaseException.java    # Base exception (unchanged)
    └── TicketPurchaseException.java     # Detailed exception messages
```

## Quality Assurance

**Professional Code Analysis:**
- ✅ **SonarCloud Integration** - Continuous code quality monitoring
- ✅ **Security Scanning** - Zero vulnerabilities detected
- ✅ **Performance Analysis** - Optimized single-traversal algorithms
- ✅ **Test Coverage** - 95% instruction coverage (JaCoCo) / 92.5% (SonarCloud)
- ✅ **Code Quality** - All "A" ratings across security, reliability, maintainability

## Tech Stack

- **Java 21** with modern features (streams, functional interfaces, method references)
- **JUnit 5** for testing framework
- **Mockito** for mocking external dependencies
- **JaCoCo** for code coverage reporting
- **Maven** for build management
- **Docker** for containerization
- **SLF4J + Logback** for logging

## Assumptions Made

- All valid accounts have sufficient funds
- External payment and seat reservation services are reliable
- One infant per adult maximum (infants sit on laps)
- Seat reservation happens before payment (better UX if payment fails)
- Business rules are enforced strictly with clear error messages

---