# College Management System
 
A comprehensive microservices-based College Management System built with Spring Boot, designed to handle all aspects of college operations including user management, course management, student enrollment, faculty management, library operations, attendance tracking, and role-based dashboards.
 
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
 
College Management System is an enterprise-grade solution designed to streamline college operations. It provides a scalable, microservices-based architecture with independent services for different functionalities, enabling easy maintenance, deployment, and scaling.
 
### Key Objectives
 
- Streamline user management (Admin, Faculty, Student, Librarian)
- Manage courses and subjects efficiently
- Track student attendance and academic progress
- Manage library book inventory and circulation
- Provide role-based dashboards for different users
- Ensure secure authentication and authorization
- Enable seamless inter-service communication
 
---
 
## Features
 
### Core Features
 
#### User Management
- Create and manage users with different roles (Admin, Faculty, Student, Librarian)
- Secure password management with reset functionality
- Email verification using OTP
- University ID generation
- Role-based access control
- User profile management
 
#### Course Management
- Create, update, and delete courses
- Assign courses to faculty members
- Track student enrollments
- Course categorization by department
- Faculty-Course mapping
 
#### Faculty Management
- Faculty profile management
- Course assignment tracking
- Attendance marking capabilities
- Dashboard with assigned courses and student count
- Schedule management
- Book issue/return tracking
 
#### Student Management
- Student enrollment in courses and subjects
- Semester and batch management
- Attendance tracking
- Subject enrollment and updates
- Book issue/return tracking
- Personal dashboard with progress metrics
 
#### Attendance Management
- Mark attendance by faculty
- Attendance percentage calculation
- Course-wise attendance tracking
- Date-range based attendance reports
- Attendance status (Present, Absent, Leave)
 
#### Library Management
- Book inventory management
- Issue and return operations
- Fine calculation for overdue books
- Member management
- Book search and filtering
- Issue history tracking
 
#### Department Management
- Create and manage departments
- Department-wise faculty assignment
- Subject allocation by department
- Department statistics
 
#### Dashboard & Analytics
- Admin dashboard with system overview
- Student-specific dashboard
- Faculty-specific dashboard
- Librarian-specific dashboard
- Key metrics and statistics
 
### Additional Features
- JWT-based authentication
- Service discovery using Eureka
- Inter-service communication via OpenFeign
- Comprehensive API documentation with Springdoc OpenAPI
- Transactional integrity
- Soft delete for data preservation
- Audit trails (created_by, updated_by)
 
---
 
## Technology Stack
 
### Backend Framework
| Technology | Version | Purpose |
|-----------|---------|---------|
| Java | 17 | Programming Language |
| Spring Boot | 3.5.13 | Framework |
| Spring Cloud | 2025.0.2 | Microservices |
| Spring Data JPA | Latest | ORM |
| Spring Security | Latest | Authentication & Authorization |
| Spring Mail | Latest | Email notifications |
 
### Database & Persistence
| Technology | Purpose |
|-----------|---------|
| MySQL | Relational Database |
| Hibernate | ORM Framework |
| Liquibase | Database Versioning (optional) |
 
### Communication & Discovery
| Technology | Purpose |
|-----------|---------|
| Eureka | Service Discovery |
| OpenFeign | Microservice Communication |
| REST API | API Communication |
 
### Security & Utilities
| Technology | Purpose |
|-----------|---------|
| JWT (JJWT) | Token-based Authentication |
| Spring Security | Authorization |
| Lombok | Boilerplate Reduction |
| ModelMapper | DTO Mapping |
| Validation | Input Validation |
 
### Documentation & Testing
| Technology | Purpose |
|-----------|---------|
| Springdoc OpenAPI | API Documentation |
| Swagger UI | Interactive API Documentation |
| JUnit | Unit Testing |
| Mockito | Mocking Framework |
 
### Build & Deployment
| Technology | Purpose |
|-----------|---------|
| Maven | Build Tool |
| Git | Version Control |
| Docker | Containerization (optional) |
 
---
 
## System Architecture
 
### Microservices Architecture