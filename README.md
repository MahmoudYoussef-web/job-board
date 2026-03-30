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
```

* Business logic isolated in Service layer
* DTOs used to prevent entity exposure
* Stateless authentication using JWT

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

http://localhost:8080/swagger-ui/index.html

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
