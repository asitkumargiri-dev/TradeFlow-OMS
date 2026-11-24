# TradeFlow OMS

A RESTful Order Management System built with Java, Spring Boot, and PostgreSQL. The application provides order management APIs with JWT-based authentication, input validation, exception handling, and Docker-based deployment.

## Tech Stack

| Technology | Purpose |
|---|---|
| Java 21 | Programming language |
| Spring Boot 4.2.0 | Backend framework |
| Spring Data JPA / Hibernate | Persistence layer |
| PostgreSQL 18 | Relational database |
| Spring Security | Application security |
| JWT | Token-based authentication |
| BCrypt | Password hashing |
| Maven | Build and dependency management |
| JUnit 5 & Mockito | Unit testing |
| Docker & Docker Compose | Containerization |
| Postman | API testing |

## Features

### Order Management
- Create trading orders
- Retrieve all orders
- Retrieve an order by ID
- Update existing orders
- Cancel pending orders
- Delete orders
- Manage order status

### Authentication & Security
- User registration and login
- Password hashing using BCrypt
- JWT-based authentication
- One-hour JWT token expiration
- Protected order management endpoints
- HTTP 401 responses for unauthenticated requests
- Duplicate username and email protection

### Backend Engineering
- RESTful API architecture
- DTO-based request and response handling
- Bean validation
- Global exception handling
- PostgreSQL database integration
- Unit testing with JUnit 5 and Mockito

### Docker
- Multi-stage Docker build
- Dockerized Spring Boot application
- PostgreSQL container
- Docker Compose orchestration
- Persistent database volume
- Environment-based configuration

## Project Structure

```text
TradeFlow-OMS/
├── src/
│   ├── main/
│   │   ├── java/com/tradeflow/
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── repository/
│   │   │   ├── entity/
│   │   │   ├── dto/
│   │   │   ├── exception/
│   │   │   ├── security/
│   │   │   └── TradeFlowApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── Dockerfile
├── docker-compose.yml
├── .dockerignore
├── .gitignore
├── .env.example
├── pom.xml
└── README.md
```

## Getting Started

### Prerequisites

- Docker Desktop
- Docker Compose
- Java 21 (required for running tests locally)

### 1. Clone the Repository

```bash
git clone https://github.com/asitkumargiri-dev/TradeFlow-OMS.git
cd TradeFlow-OMS
```

### 2. Configure Environment Variables

Create a `.env` file in the project root using `.env.example` as a template.

**Windows PowerShell:**

```powershell
Copy-Item .env.example .env
```

**Linux / macOS:**

```bash
cp .env.example .env
```

Open the newly created `.env` file and configure the following variables:

```env
DB_PASSWORD=your_database_password
JWT_SECRET=your_base64_encoded_secret
```

Generate a secure JWT secret using at least 32 random bytes encoded in Base64.

**Linux / macOS / Git Bash:**

```bash
openssl rand -base64 32
```

**Windows PowerShell alternative:**

```powershell
$bytes = New-Object byte[] 32
$rng = [Security.Cryptography.RandomNumberGenerator]::Create()
$rng.GetBytes($bytes)
$rng.Dispose()
[Convert]::ToBase64String($bytes)
```

Copy the generated value into the `JWT_SECRET` variable in your `.env` file.

**Important:** Never commit your actual `.env` file, database password, or JWT secret to GitHub. Only the `.env.example` template containing placeholder values should be committed.

### 3. Start the Application

```bash
docker compose up --build -d
```

Docker Compose builds the Spring Boot application and starts the application and PostgreSQL containers.

Application URL:

```text
http://localhost:8080
```

PostgreSQL is exposed on host port `5433`.

### 4. Stop the Application

```bash
docker compose down
```

The PostgreSQL data volume is retained when using this command.

## API Endpoints

All order endpoints require a valid JWT token.

### Authentication

| Method | Endpoint | Description | Access |
|---|---|---|---|
| POST | `/api/auth/register` | Register a new user | Public |
| POST | `/api/auth/login` | Authenticate and generate JWT | Public |

### Order Management

| Method | Endpoint | Description | Access |
|---|---|---|---|
| POST | `/api/orders` | Create an order | JWT Required |
| GET | `/api/orders` | Retrieve all orders | JWT Required |
| GET | `/api/orders/{id}` | Retrieve an order by ID | JWT Required |
| PUT | `/api/orders/{id}` | Update an order | JWT Required |
| PUT | `/api/orders/{id}/cancel` | Cancel a pending order | JWT Required |
| DELETE | `/api/orders/{id}` | Delete an order | JWT Required |

## Sample API Requests

### 1. Register a User

`POST /api/auth/register`

```json
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "Password123"
}
```

### 2. Login

`POST /api/auth/login`

```json
{
  "username": "testuser",
  "password": "Password123"
}
```

A successful login returns an authentication response containing a JWT token and username.

Example response structure:

```json
{
  "token": "<your-jwt-token>",
  "username": "testuser",
  "message": "<response-message>"
}
```

### 3. Create an Order

`POST /api/orders`

Include the JWT in the Authorization header:

```http
Authorization: Bearer <your-jwt-token>
```

Request body:

```json
{
  "symbol": "NIFTY",
  "orderType": "BUY",
  "quantity": 50,
  "price": 24500.50
}
```

### 4. Retrieve Orders

`GET /api/orders`

A valid JWT token is required. Requests without valid authentication receive HTTP `401 Unauthorized`.

## Order Lifecycle

- New orders are created with `PENDING` status.
- Only pending orders can be updated or cancelled.
- Cancelled orders cannot be cancelled again or updated.

## Running Tests

Execute the unit tests using Maven.

**Windows PowerShell:**

```powershell
.\mvnw.cmd test
```

**Linux / macOS:**

```bash
./mvnw test
```

The project includes JUnit 5 and Mockito unit tests for order service functionality.

## Future Enhancements

- Pre-trade risk management
- Advanced order execution workflow
- Order status history / audit trail
- Additional integration tests

## Author

Developed as a hands-on Java backend engineering project.

GitHub: https://github.com/asitkumargiri-dev