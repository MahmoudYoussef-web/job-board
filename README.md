# 💼 Job Platform System (Spring Boot)

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/SpringBoot-3.x-green)
![MySQL](https://img.shields.io/badge/MySQL-8-blue)
![JWT](https://img.shields.io/badge/Auth-JWT-yellow)
![Status](https://img.shields.io/badge/Status-Production--Ready-brightgreen)

A production-grade backend system for a job platform that connects employers with candidates, with strong focus on **job management, application lifecycle, and business rule enforcement**.

---

## 🎯 Project Overview

This system simulates a real-world job platform where:

* Employers create and manage job postings
* Candidates apply for jobs
* Applications go through controlled lifecycle states
* The system enforces strict business rules and access control

> The goal is to demonstrate real backend engineering: **not just CRUD, but system design**

---

## 🚀 Key Features

### 🔐 Authentication & Security

* JWT Authentication (Access + Refresh Tokens)
* Role-Based Authorization (EMPLOYER / CANDIDATE)
* Stateless security model

### 💼 Job Management

* Create, update, delete jobs
* Advanced filtering (title, location, salary, experience)
* Pagination & sorting
* Employer-specific job management

### 📄 Application System

* Apply to jobs
* Track application status (PENDING / ACCEPTED / REJECTED)
* Employer can update application status
* Prevent duplicate applications

### 📂 File Handling

* Resume upload system
* File metadata tracking

### ⚙️ System Design

* DTO-based architecture
* Centralized exception handling
* Clean layered architecture

---

## 🏗️ Architecture

```text
Client
   ↓
Controller → Service → Repository → Database
   ↓
Security Layer (JWT)
   ↓
Exception Handling Layer
````

* Business logic isolated in Service layer
* DTOs used to prevent entity exposure
* Stateless authentication using JWT

---

## 🧠 System Design Decisions

### Why DTO Layer?

To decouple internal entity models from external API contracts and prevent overexposure of sensitive fields.

### Why JWT Authentication?

JWT enables stateless authentication, improving scalability and eliminating server-side session storage.

### Why Service Layer?

To centralize and enforce business rules consistently across the system.

### Why Pagination?

To ensure scalability and prevent performance degradation with large datasets.

### Why Layered Architecture?

To maintain separation of concerns and improve maintainability and testability.

---

## 🔐 Security Design

* All protected endpoints require:

Authorization: Bearer <token>

### Flow:

1. User logs in → receives JWT
2. JWT is validated in filter
3. Role-based authorization applied

---

## ⚙️ Business Rules

* A candidate cannot apply to the same job twice
* Only employers can create/update jobs
* Only candidates can apply
* Application status transitions are controlled
* Users can only access their own data

---

## 🧩 Core Business Logic

### 📄 Application Lifecycle

1. Candidate applies to a job
2. System validates:

    * Job is still open
    * Candidate has not applied before
3. Application created with status = `PENDING`
4. Employer reviews application
5. Status updated to:

    * `ACCEPTED`
    * `REJECTED`

### 🔐 Authorization Logic

* Employers:

    * Can manage only their own jobs
    * Can view applications for their jobs only

* Candidates:

    * Can apply to jobs
    * Can view only their own applications

### 🚫 Validation Rules

* Duplicate applications are blocked at service level
* Invalid state transitions are rejected
* Unauthorized access throws proper exceptions

---

## 💡 What Makes This Project Different?

* Not just CRUD — real-world business rules enforced
* Controlled application lifecycle with strict state transitions
* Role-based access control implemented at service level
* Production-oriented architecture (DTO, exception handling, security)
* Clean separation between layers

> This project focuses on **real backend engineering**, not just endpoints.

---

## 📁 Project Structure

```text
src/main/java/com/jobboard/

├── controller
├── service
├── repository
├── entity
├── dto
├── mapper
├── security
├── exception
```

---

## 🛠️ Tech Stack

| Category   | Technology            |
| ---------- | --------------------- |
| Language   | Java 21               |
| Framework  | Spring Boot 3         |
| Security   | Spring Security + JWT |
| ORM        | Spring Data JPA       |
| Database   | MySQL                 |
| Docs       | Swagger (OpenAPI)     |
| Build Tool | Maven                 |

---

## ▶️ How to Run

### 🔧 Prerequisites

* Java 21
* Maven
* MySQL 8

### 🚀 Steps

```bash
git clone https://github.com/your-username/job-platform.git
cd job-platform
mvn clean install
mvn spring-boot:run
```

### ⚙️ Database Configuration

Update `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/job_platform
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

---

## 🌐 API Endpoints (Swagger-Based)

### 🔐 Authentication

* POST `/api/auth/register`
* POST `/api/auth/login`
* POST `/api/auth/refresh`

### 👤 Users

* GET `/api/users/me`
* PUT `/api/users/me`

### 💼 Jobs

* GET `/api/jobs`
* GET `/api/jobs/{id}`
* POST `/api/jobs`
* PUT `/api/jobs/{id}`
* DELETE `/api/jobs/{id}`
* PATCH `/api/jobs/{id}/close`
* GET `/api/jobs/mine`

### 📄 Applications

* POST `/api/jobs/{jobId}/apply`
* PATCH `/api/applications/{id}/status`
* GET `/api/jobs/{jobId}/applications`
* GET `/api/applications/me`

### 📂 Files

* POST `/api/files/resume`

---

## 📸 API Documentation

Swagger UI available at:

[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## 🧪 Try the API

After running the application:

👉 [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

You can test:

* Authentication
* Jobs
* Applications
* Users

---

## 🧱 Detailed Architecture

```text
[Client]
   ↓
[Controller Layer]
   ↓
[Service Layer] → Business Rules Enforcement
   ↓
[Repository Layer]
   ↓
[Database]

[Security Layer]
- JWT Filter
- Authentication Provider

[Exception Layer]
- Global Exception Handler
```

---

## ⚠️ Error Handling

Centralized exception handling using @RestControllerAdvice

### Response Example:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "timestamp": "2026-03-30T17:48:35"
}
```

---

## 🧠 Engineering Highlights

* Clean layered architecture
* DTO pattern to protect domain models
* Stateless JWT authentication
* Centralized exception handling
* Strong business rule enforcement
* Pagination and filtering support

---

## 🧠 System Design Highlights

* Pagination support for scalability
* Role-based access control
* Clean DTO mapping
* Controlled state transitions
* Secure file handling

---

## 💣 Why This Project Matters

* Demonstrates real backend engineering
* Includes business rules (not CRUD)
* Covers authentication, authorization, and file handling
* Designed with production mindset

---

## 👨‍💻 Author

Mahmoud Youssef
Backend Developer (Spring Boot)

---

## 🎯 Final Result

A backend system that is:

* ✅ Secure
* ✅ Scalable
* ✅ Maintainable
* ✅ Production-ready



