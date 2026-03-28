# 🗳️ Online Voting System (Spring Boot)

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/SpringBoot-3.x-green)
![MySQL](https://img.shields.io/badge/MySQL-8-blue)
![JWT](https://img.shields.io/badge/Auth-JWT-yellow)
![Status](https://img.shields.io/badge/Status-Production--Ready-brightgreen)

A secure, role-based online voting system built using Spring Boot, designed to simulate real-world digital elections with strong emphasis on **security, data integrity, and business rule enforcement**.

---

## 🎯 Project Overview

This system models a real-world election platform where:

* Admins create and manage elections
* Voters participate securely in voting
* The system guarantees **one vote per user**
* Voting is restricted to a **specific time window**
* Results are calculated dynamically and accurately

> The goal is to demonstrate backend system design, authentication, authorization, and enforcement of critical business rules.

---

## 🚀 Key Features

* JWT Authentication (Access Token)
* Role-Based Authorization (ADMIN / VOTER)
* Election creation with time window
* Candidate registration per election
* Voter assignment to elections
* One-time voting enforcement
* Real-time result calculation
* Input validation & structured error handling

---

## 🏗️ Architecture

The system follows a **layered architecture**:

```text
Client
   ↓
Controller → Service → Repository → Database
   ↓
Security Layer (JWT Filter)
   ↓
Exception Handling Layer
```

### Layers:

* **Controller Layer**

    * Handles HTTP requests and responses

* **Service Layer**

    * Contains business logic and rules enforcement

* **Repository Layer**

    * Data access via Spring Data JPA

* **Security Layer**

    * JWT authentication & authorization filters

### Key Design Decisions:

* Separation of concerns
* DTO pattern for API contracts
* Centralized exception handling
* Stateless authentication using JWT

---

## 🔐 Security Design

* Stateless authentication using JWT
* All protected endpoints require:

```
Authorization: Bearer <token>
```

### Flow:

1. User logs in → receives JWT
2. JWT is sent in request header
3. Security filter validates token
4. Role-based access control is applied

---

## ⚙️ Business Rules

* A voter can vote **only once per election**
* Voting allowed only between:

    * `startTime` and `endTime`
* Only assigned voters can participate
* Admin-only operations:

    * Create elections
    * Assign voters
    * Add candidates
    * View results

---

## 🧠 Challenges & Solutions

### Preventing Duplicate Voting

* Enforced via:

    * Service-layer validation
    * Unique constraint (voter + election)

---

### Enforcing Voting Time Window

* Compared current time with election window
* Returned `403 Forbidden` if invalid

---

### Securing Endpoints

* Implemented JWT filter
* Restricted access based on roles

---

## 📁 Project Structure

```text
src/main/java/com/onlinevotingsystem/
│
├── model/        → JPA entities
├── dto/          → Request & Response DTOs
├── repository/   → JPA repositories
├── service/      → Business logic
├── controller/   → REST APIs
├── security/     → JWT & Security config
├── exception/    → Global exception handling
└── main class
```

---

## 🛠️ Tech Stack

| Category   | Technology                  |
| ---------- |-----------------------------|
| Framework  | Spring Boot                 |
| Language   | Java  21                    |
| Security   | Spring Security + JWT       |
| ORM        | Spring Data JPA (Hibernate) |
| Database   | MySQL / H2                  |
| Validation | Jakarta Validation          |
| API Docs   | Swagger (OpenAPI)           |
| Build Tool | Maven                       |

---

## 🧪 Running the Project

### 1. Clone Repository

```bash
git clone <your-repo-link>
cd online-voting-system
```

---

### 2. Configure Database

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/voting_system
spring.datasource.username=your_username
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

---

### 3. Run Application

```bash
mvn spring-boot:run
```

---

## 🌐 API Endpoints

### 🔑 Authentication

| Method | Endpoint            | Description     |
| ------ | ------------------- | --------------- |
| POST   | /api/auth/login     | Login & get JWT |
| POST   | /api/users/register | Register user   |

---

### 🗳️ Election (Admin)

| Method | Endpoint                 | Description     |
| ------ | ------------------------ | --------------- |
| POST   | /api/admin/elections     | Create election |
| POST   | /api/admin/voters/assign | Assign voter    |

---

### 🎯 Candidate

| Method | Endpoint              | Description     |
| ------ | --------------------- | --------------- |
| POST   | /api/admin/candidates | Add candidate   |
| GET    | /api/voter/candidates | View candidates |

---

### 📝 Voting

| Method | Endpoint        | Description |
| ------ | --------------- | ----------- |
| POST   | /api/voter/vote | Cast vote   |

---

### 📊 Results (Admin)

| Method | Endpoint           | Description  |
| ------ | ------------------ | ------------ |
| GET    | /api/admin/results | View results |

---

## 📸 API Documentation

Interactive API documentation available via Swagger:

```text
http://localhost:8080/swagger-ui/index.html
```

---

## ⚠️ Error Handling & Exception Strategy

The system implements a **centralized exception handling mechanism** using `@RestControllerAdvice`.

---

### 🧱 Error Response Structure

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/example",
  "timestamp": "2026-03-27T19:56:01"
}
```

---

### 🔍 Validation Errors Example

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "validationErrors": {
    "email": "must be valid",
    "password": "min 8 chars"
  }
}
```

---

### 🧠 Exception Handling Coverage

| Exception Type                   | HTTP Status | Description               |
| -------------------------------- | ----------- | ------------------------- |
| ResourceNotFoundException        | 404         | Resource not found        |
| DuplicateApplicationException    | 409         | Prevent duplicate voting  |
| InvalidStatusTransitionException | 422         | Invalid state change      |
| ForbiddenOperationException      | 403         | Forbidden action          |
| UnauthorizedException            | 403         | Custom access restriction |
| BusinessException                | 400         | Business rule violation   |
| FileUploadException              | 400         | File validation error     |
| BadCredentialsException          | 401         | Invalid credentials       |
| DisabledException                | 403         | Disabled account          |
| AccessDeniedException            | 403         | Role-based denial         |
| DataIntegrityViolationException  | 409         | DB constraint violation   |
| MaxUploadSizeExceededException   | 400         | File too large            |
| MethodArgumentNotValidException  | 400         | Validation failure        |
| Exception                        | 500         | Unexpected error          |

---

### 📂 Logging Strategy

* `WARN` → business & validation errors
* `ERROR` → system failures

Example:

```text
WARN  - Duplicate vote attempt
ERROR - Unexpected system error
```

---

## 🧠 System Design Summary

```text
Client
   ↓
Controller Layer
   ↓
Service Layer (Business Logic)
   ↓
Repository Layer (JPA)
   ↓
Database

   ↓
Security Layer (JWT)
   ↓
Exception Handling Layer
```

---

## 💣 Why This Project Matters

* Demonstrates real backend engineering skills
* Focuses on business rules, not just CRUD
* Implements secure authentication
* Handles real-world edge cases
* Built with production mindset

---

## 👨‍💻 Author

**Mahmoud Youssef**

Backend Developer specializing in Spring Boot and secure system design.

---

### 🧠 Project Ownership

This project was fully developed independently, including:

* System architecture design
* Security implementation (JWT)
* Business logic enforcement
* Database modeling
* Exception handling
* API design

---

## 🎯 Final Result

A backend system that is:

* ✅ Secure
* ✅ Scalable
* ✅ Maintainable
* ✅ Production-ready
