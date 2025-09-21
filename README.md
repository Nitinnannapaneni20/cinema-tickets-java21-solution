# Cinema Ticket Booking System

This is an implementation of a cinema ticket booking system. It handles ticket purchases with validation, payment processing, and seat reservations.

## Ticket Rules

- Adults cost £25, Children cost £15, Infants are free
- You can buy max 25 tickets at once
- Kids and babies need at least one adult with them
- Babies sit on adult laps (no separate seat needed)
- Only valid account IDs (greater than 0) can buy tickets

## Setup Instructions

### Check if you have the right Java version
```bash
  java -version
```
You need Java 21. If you don't have it:

**On Ubuntu/Debian:**
```bash
  sudo apt update
  sudo apt install openjdk-21-jdk
```

**On macOS (with Homebrew):**
```bash
  brew install openjdk@21
```

**On Windows:**
Download from Oracle or use Chocolatey:
```bash
  choco install openjdk21
```

### Check if you have Maven
```bash
  mvn -version
```
If not installed:

**On Ubuntu/Debian:**
```bash
  sudo apt install maven
```

**On macOS:**
```bash
  brew install maven
```

**On Windows:**
Download from Apache Maven website or:
```bash
  choco install maven
```

#### Running the tests -   Clone the repo and navigate to it
```bash
  cd cinema-tickets-java21-solution
```

#### Run all tests
```bash
  mvn clean test
```

## How I built it

- All validation happens first before calling any external services
- Seats get reserved before payment (makes more sense business-wise)
- Used proper exception handling for invalid requests
- Kept the external services as they were (couldn't modify them)
- Added comprehensive tests covering real family scenarios

## What I tested

- 16 test cases covering different scenarios
- Happy path: families buying tickets, adults only, boundary cases
- Error cases: invalid accounts, business rule violations, edge cases
- Real scenarios: large families, babies with parents, etc.

## Assumptions I made

- All valid accounts have enough money
- The payment and seat services always work
- Babies can only sit on adult laps (1 baby per adult max)
- It makes sense to reserve seats before charging money

## Project structure

The main code is in `TicketServiceImpl.java` - that's where all the logic lives.
Everything else was provided in the base project.

Tests are in `TicketServiceImplTest.java` with realistic scenarios.

## Tech used

- Java 21
- JUnit 5 for testing
- Mockito for mocking the external services
- Maven for building

---

If you have any issues running this, check that Java 21 and Maven are properly installed and in your PATH.