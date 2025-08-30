# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Structure

This is a Java Maven project with both API and UI testing capabilities:

- **API Tests**: Located in `src/test/java/api/` - Tests for the Reqres API using RestAssured
- **UI Tests**: Located in `src/test/java/ui/` - Selenium WebDriver tests with Page Object Model
- **Main Architecture**: 
  - API client (`ReqresClient.java`) provides methods for all API operations
  - UI tests use a singleton `DriverFactory` with thread-safe WebDriver management
  - Page Object Model implementation in `src/test/java/ui/PageObjects/`

## Commands

### Build and Test
- **Run all tests**: `mvn test`
- **Run specific test class**: `mvn test -Dtest=UserTests`
- **Run specific test method**: `mvn test -Dtest=UserTests#testCreateUser`
- **Clean and test**: `mvn clean test`
- **Compile only**: `mvn compile`

### Docker
- **Build image**: `docker build -t my-api-tests:latest .`
- **Run tests in container**: `docker run my-api-tests:latest`

### Jenkins Integration
- Uses `Jenkinsfile` for pipeline execution
- Generates XML test reports in `target/surefire-reports/`
- Script `scanTests.sh` extracts test methods for Jenkins job creation

## Key Dependencies

- **Java 17** (compiler source/target)
- **JUnit 5** for test framework
- **RestAssured 5.2.0** for API testing
- **Selenium 4.27.0** for UI automation
- **WebDriverManager 5.6.2** for driver management
- **Gson 2.10.1** for JSON parsing
- **Lombok 1.18.36** for reducing boilerplate

## Test Architecture

### API Testing
- Base client: `api.ReqresClient` with common request specifications
- Models in `api.UsersModels/` for request/response objects
- Tests target Reqres.in API endpoints

### UI Testing
- `ui.Tests.BaseTests` provides WebDriver setup with JUnit 5 extensions
- `ui.Drivers.DriverFactory` manages WebDriver instances (Chrome headless by default)
- `ui.PageObjects.BasePage` contains common page functionality
- `ui.Utils.TestResultExtension` for test result handling

## Configuration Notes

- WebDriver runs in headless Chrome with Docker-optimized settings
- Uses implicit wait of 10 seconds
- **Thread-safe driver management**: Uses ThreadLocal for parallel test execution
- Parallel test execution enabled (3 threads per core)
- Supports Firefox, Edge, and Chrome browsers via `DriverTypes` enum
- Automatic driver cleanup after each test (success or failure)