# College Management System

A comprehensive microservices-based College Management System built with Spring Boot. It handles core college operations including user management, course management, student enrollment, faculty management, library operations, attendance tracking, dashboards, and role-based access control.

## Table of Contents

- [Project Overview](#project-overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [System Architecture](#system-architecture)
- [Prerequisites](#prerequisites)
- [Project Structure](#project-structure)
- [Setup & Installation](#setup--installation)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Database Schema](#database-schema)
- [Role-Based Access Control](#role-based-access-control)
- [Service Endpoints](#service-endpoints)
- [Development Guidelines](#development-guidelines)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [License](#license)

---

## Project Overview

College Management System is an enterprise-grade distributed application designed to streamline college operations through independent microservices. The architecture uses Eureka Service Discovery and OpenFeign for inter-service communication, with JWT-based authentication for secure access [file:2].

### Key Objectives

- Streamline user management for Admin, Faculty, Student, and Librarian roles.
- Manage courses, subjects, and faculty assignments efficiently.
- Track attendance and academic progress.
- Manage library books, issue/return operations, and fines.
- Provide role-based dashboards for different user types.
- Ensure secure authentication and authorization across services.

---

## Features

### Core Features

#### User Management
- Create and manage users with multiple roles.
- Secure password reset functionality.
- Email verification using OTP.
- University ID generation.
- Role-based access control.
- User profile management.

#### Course Management
- Create, update, delete, and fetch courses.
- Assign courses to faculty members.
- Track course counts and faculty-course mappings.
- Support course-wise data retrieval.

#### Faculty Management
- Faculty profile management.
- Course assignment tracking.
- Attendance marking capabilities.
- Faculty dashboard with assigned courses and student counts.

#### Student Management
- Student enrollment in courses and subjects.
- Semester and batch management.
- Attendance tracking.
- Subject updates and profile management.

#### Attendance Management
- Mark attendance by faculty.
- Calculate attendance percentage.
- Track attendance by student and by course.

#### Library Management
- Book inventory management.
- Issue and return operations.
- Fine calculation for overdue books.
- Member tracking and issue history.

#### Department Management
- Create and manage departments.
- Department-wise faculty assignment.
- Subject allocation by department.
- Department statistics.

#### Dashboard & Analytics
- Admin dashboard.
- Student dashboard.
- Faculty dashboard.
- Librarian dashboard.
- Key metrics and statistics.

### Additional Features
- JWT-based authentication.
- Service discovery using Eureka.
- Inter-service communication via OpenFeign.
- API documentation with Springdoc OpenAPI.
- Transactional integrity.
- Soft delete support.
- Audit fields like `created_by` and `updated_by`.

---

## Technology Stack

### Backend Framework
| Technology | Version | Purpose |
|---|---:|---|
| Java | 17 | Programming language |
| Spring Boot | 3.5.13 | Application framework |
| Spring Cloud | 2025.0.2 | Microservices support |
| Spring Data JPA | Latest | ORM/data access |
| Spring Security | Latest | Authentication and authorization |
| Spring Mail | Latest | Email notifications |

### Database & Persistence
| Technology | Purpose |
|---|---|
| MySQL | Relational database |
| Hibernate | ORM framework |
| Liquibase | Database versioning |

### Communication & Discovery
| Technology | Purpose |
|---|---|
| Eureka | Service discovery |
| OpenFeign | Inter-service communication |
| REST API | HTTP communication |

### Security & Utilities
| Technology | Purpose |
|---|---|
| JWT (JJWT) | Token-based authentication |
| Lombok | Boilerplate reduction |
| ModelMapper | DTO mapping |
| Validation | Input validation |

### Documentation & Testing
| Technology | Purpose |
|---|---|
| Springdoc OpenAPI | API documentation |
| Swagger UI | Interactive docs |
| JUnit | Unit testing |
| Mockito | Mocking |

### Build & Deployment
| Technology | Purpose |
|---|---|
| Maven | Build tool |
| Git | Version control |
| Docker | Containerization |

---

## System Architecture

The system follows a microservices architecture with an API Gateway, Eureka Discovery Server, and independent domain services. Requests are authenticated using JWT, then routed to the relevant service through the gateway [file:2].

### Services
- Auth Service
- User Service
- Course Service
- Faculty Service
- Student Service
- Library Service
- Department Service
- Attendance Service
- Dashboard Service
- Subject Service

### High-Level Flow
1. Client sends request to API Gateway.
2. Gateway validates JWT and routes the request.
3. Service registry resolves the target service using Eureka.
4. Services communicate using OpenFeign when needed.
5. Response is returned in a standardized JSON format.

---

## Prerequisites

Before running the project, make sure you have:

- Java 17 installed.
- Maven installed.
- MySQL 8+ running locally.
- Eureka Server running.
- IDE like IntelliJ IDEA or Eclipse.
- Postman for API testing.

---

## Project Structure

```text
college-management-system/
├── api-gateway/
├── auth-service/
├── user-service/
├── course-service/
├── faculty-service/
├── student-service/
├── library-service/
├── department-service/
├── attendance-service/
├── dashboard-service/
├── subject-service/
└── eureka-server/
```

---

## Setup & Installation

### 1. Clone the repository
```bash
git clone https://github.com/your-username/college-management-system.git
cd college-management-system
```

### 2. Configure MySQL
Create databases for each service or a shared database, based on your setup.

### 3. Update configuration
Set your database credentials, Eureka URL, and service ports in each service’s `application.yml`.

### 4. Build the project
```bash
mvn clean install
```

---

## Configuration

Each microservice should include settings like:

```yml
spring:
  application:
    name: auth-service
  datasource:
    url: jdbc:mysql://localhost:3306/auth_db
    username: root
    password: root
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka

server:
  port: 8001
```

### Service Ports
- Auth Service: `8001`
- User Service: `8002`
- Course Service: `8003`
- Faculty Service: `8004`
- Student Service: `8005`
- Library Service: `8006`
- Department Service: `8007`
- Attendance Service: `8008`
- Dashboard Service: `8009`
- Subject Service: `8010`
- Eureka Server: `8761`

---

## Running the Application

Start the services in this order:

1. Eureka Server
2. API Gateway
3. Auth Service
4. User Service
5. Course Service
6. Faculty Service
7. Student Service
8. Library Service
9. Department Service
10. Attendance Service
11. Dashboard Service
12. Subject Service

Example:
```bash
cd eureka-server
mvn spring-boot:run
```

Repeat for each service.

---

## API Documentation

All APIs follow a standardized response format.

### Success Response
```json
{
  "status": 200,
  "message": "Operation successful",
  "data": {},
  "timestamp": "2026-04-21T10:00:00.000Z"
}
```

### Error Response
```json
{
  "status": 400,
  "message": "Error message here",
  "reason": "Additional error reason if applicable",
  "timestamp": "2026-04-21T10:00:00.000Z"
}
```

Interactive documentation is available through Swagger UI for each service.

---

## Database Schema

The system includes tables for:
- users
- admins
- faculty
- students
- courses
- subjects
- departments
- attendance_records
- books
- librarians
- book_issue
- password_reset_token
- otp
- blacklisted_tokens

The schema supports soft delete, audit fields, token tracking, and role-specific data storage [file:2].

---

## Role-Based Access Control

### Admin
- Manage users
- Manage departments
- Manage courses
- Manage subjects
- View all dashboards

### Faculty
- View own profile
- View assigned courses
- Mark attendance
- View assigned student records
- Update own password

### Student
- View own profile
- View enrolled courses
- View attendance records
- View library books
- Issue and return books

### Librarian
- Manage book inventory
- Track book issues and returns
- View library statistics
- Create librarian accounts

---

## Service Endpoints

### Auth Service
- `POST /api/auth/login`
- `GET /api/auth/validate`
- `POST /api/auth/logout`
- `POST /api/auth/forgot-password`
- `POST /api/auth/generate-otp`
- `POST /api/auth/verify-otp`
- `POST /api/auth/reset-password`

### User Service
- `POST /api/users`
- `PUT /api/users/university/{universityId}`
- `PUT /api/users/university/{universityId}/password`
- `DELETE /api/users/university/{universityId}`

### Course Service
- `POST /api/courses`
- `GET /api/courses`
- `GET /api/courses/{id}`
- `GET /api/courses/count`
- `PUT /api/courses/{code}`
- `DELETE /api/courses/{code}`
- `POST /api/courses/assign/faculty/{facultyUniversityId}`
- `GET /api/courses/faculty/{facultyUniversityId}`

### Faculty Service
- `POST /api/faculty`
- `GET /api/faculty/{facultyUniversityId}`
- `GET /api/faculty`
- `PUT /api/faculty/{facultyUniversityId}`
- `DELETE /api/faculty/{facultyUniversityId}`
- `GET /api/faculty/{facultyUniversityId}/dashboard`
- `GET /api/faculty/{facultyUniversityId}/courses`

### Student Service
- `POST /api/students`
- `GET /api/students/university-id/{universityId}`
- `GET /api/students`
- `PUT /api/students/{universityId}`
- `PUT /api/students/{universityId}/semester`
- `PUT /api/students/{universityId}/subjects`

### Library Service
- `POST /api/library/books`
- `GET /api/library/books`
- `GET /api/library/books/{bookId}`
- `PUT /api/library/books/{bookId}`
- `PUT /api/library/books/{bookId}/quantity`
- `DELETE /api/library/books/{bookId}`
- `POST /api/library/issue`
- `GET /api/library/members/count`
- `GET /api/library/members`

### Department Service
- `POST /api/departments`
- `GET /api/departments`
- `GET /api/departments/{id}`
- `GET /api/departments/code/{code}`
- `PUT /api/departments/{id}`
- `DELETE /api/departments/{id}`
- `GET /api/departments/count/total`

### Attendance Service
- `POST /api/attendance`
- `GET /api/attendance/student/{studentId}`
- `GET /api/attendance/course/{courseId}/date/{date}`
- `GET /api/attendance/student/{studentId}/percentage`

### Dashboard Service
- `GET /api/dashboard/admin`
- `GET /api/dashboard/student/{universityId}`
- `GET /api/dashboard/faculty/{facultyUniversityId}`
- `GET /api/dashboard/librarian`
- `GET /api/dashboard/profile/me`

### Subject Service
- `POST /api/subjects`
- `GET /api/subjects/code/{code}`

---

## Development Guidelines

- Follow layered architecture: controller, service, repository, entity, DTO.
- Use validation on request payloads.
- Keep API responses standardized.
- Use meaningful exception handling.
- Prefer DTOs over exposing entities directly.
- Maintain role-based security checks for protected endpoints.

---

## Troubleshooting

### Common Issues
- **Eureka not connecting**: ensure Eureka Server is running on port 8761.
- **Database connection error**: verify MySQL credentials and database URL.
- **401 Unauthorized**: check JWT token validity and headers.
- **404 Not Found**: confirm service registration and endpoint path.
- **Feign call failure**: ensure the target service is up and registered.

---

## Contributing

1. Fork the repository.
2. Create a feature branch.
3. Commit your changes.
4. Push the branch.
5. Create a pull request.

---

## License

This project is licensed under the HTI License.