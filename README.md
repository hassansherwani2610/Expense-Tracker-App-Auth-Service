# ETA Auth Service

Enterprise Token Authentication (ETA) – Secure Authentication & Authorization Microservice built using Spring Boot and Spring Security.

---

## 📌 Overview

ETA Auth Service is a production-ready backend authentication system implementing **JWT-based authentication with Refresh Token support**. The project follows clean architecture principles and demonstrates secure API design suitable for microservices and enterprise-grade applications.

It provides:
- User registration & login
- JWT access token generation
- Refresh token lifecycle management
- Role-based authorization
- Stateless authentication using Spring Security

---

## 🛠 Tech Stack

- Java 17+
- Spring Boot
- Spring Security
- JWT (JSON Web Tokens)
- Refresh Token Strategy
- Spring Data JPA
- Hibernate
- MySQL
- Gradle
- Lombok

---

## 🏗 Architecture

The project follows a layered architecture:

```
Controller Layer
      ↓
Service Layer
      ↓
Repository Layer
      ↓
Database (MySQL)
```

Security Flow:

```
Client → AuthController → AuthenticationManager
        → JWT Generated
        → JwtAuthFilter validates token on protected endpoints
```

---

## 📂 Project Structure

```
ETA-Project/
├── src/main/java/com/eta/authservice/
│
│   ├── App.java
│   │   └── Spring Boot application entry point
│
│   ├── auth/
│   │   ├── JwtAuthFilter.java
│   │   │   └── Validates JWT token for each request
│   │   ├── SecurityConfig.java
│   │   │   └── Spring Security configuration
│   │   └── UserConfig.java
│   │       └── Authentication & bean configuration
│
│   ├── controller/
│   │   ├── AuthController.java
│   │   │   └── Handles registration & login
│   │   └── TokenController.java
│   │       └── Handles refresh token generation
│
│   ├── entities/
│   │   ├── UserInfo.java
│   │   │   └── User entity mapped to database
│   │   ├── RefreshToken.java
│   │   │   └── Stores refresh tokens with expiry
│   │   └── UserRole.java
│   │       └── Enum for role-based access
│
│   ├── repository/
│   │   ├── UserRepository.java
│   │   └── RefreshTokenRepository.java
│
│   ├── service/
│   │   ├── UserDetailsServiceImpl.java
│   │   ├── CustomUserDetails.java
│   │   ├── JwtService.java
│   │   └── RefreshTokenService.java
│
│   ├── request/
│   │   ├── AuthRequestDto.java
│   │   └── RefreshTokenRequestDto.java
│
│   ├── response/
│   │   └── JwtResponseDto.java
│
│   └── utils/
│       └── ValidationUtil.java
│
├── src/main/resources/
│   └── application.properties
│
├── build.gradle
└── settings.gradle
```

---

## 🔐 Security Features

- Password encryption using Spring Security
- Stateless authentication
- JWT access tokens
- Refresh token stored in database
- Role-based authorization (USER / ADMIN)
- Custom authentication filter
- Token validation on each secured request

---

## 🔄 Authentication Flow

1. User registers
2. User logs in
3. Server generates:
   - Access Token (short-lived)
   - Refresh Token (stored in DB)
4. Access token is used for protected APIs
5. When expired → Refresh token generates new access token

---

## 📡 API Endpoints

| Method | Endpoint | Description |
|--------|----------|------------|
| POST | `/auth/v1/signup` | Register new user |
| POST | `/auth/v1/login` | Login & generate JWT |
| POST | `/auth/v1/refreshToken` | Generate new access token |

---

## ⚙️ Configuration

Database configuration is defined in:

```
src/main/resources/application.properties
```

Example:

```
spring.datasource.url=jdbc:mysql://localhost:3306/your_db
spring.datasource.username=root
spring.datasource.password=your_password
```

---

## 🧪 Testing

APIs can be tested using:

- Postman
- cURL
- Any REST client

---

## 📬 Postman Collection

A complete Postman Collection is included to demonstrate the full authentication workflow.

### Included Requests

- User Registration (`/auth/register`)
- User Login (`/auth/login`)
- Access Protected Endpoint (Bearer Token required)
- Refresh Access Token (`/auth/refresh`)

### How to Use

1. Open Postman
2. Click Import
3. Upload `AuthService.postman_collection.json`
4. Set environment variable:

```
base_url = http://localhost:8080
```

5. Execute in order:
   - Register
   - Login
   - Access secured endpoint
   - Refresh token

Environment variables used:

- `base_url`
- `access_token`
- `refresh_token`

This demonstrates the complete JWT authentication lifecycle and reflects production-level API documentation practice.

---

## 🚀 Running the Project

### Prerequisites

- Java 17+
- MySQL
- Gradle

### Steps

```
git clone <your-repo-url>
cd ETA Project
./gradlew bootRun
```

Application runs on:

```
http://localhost:8080
```

---

## 📈 Why This Project Matters

This project demonstrates:

- Deep understanding of Spring Security
- JWT implementation from scratch
- Refresh token lifecycle management
- Secure REST API design
- Clean layered architecture
- Production-ready authentication design

---

## 👨‍💻 Developer

Hassan Sherwani  
Backend Developer | Spring Boot | Security | Microservices

