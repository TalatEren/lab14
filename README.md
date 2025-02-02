# Secure Blog Application

This is a secure Java Spring REST API application that allows users to create, read, update, and delete blog posts. The application implements various security measures to ensure data protection and proper user management.

## Features

- User Authentication (Registration & Login)
- JWT-based Authorization
- Blog Post Management (CRUD operations)
- Input Validation
- Secure Password Storage
- Unit and Integration Tests

## Security Measures

### Authentication & Authorization
- JWT (JSON Web Token) based authentication
- Token-based session management
- Protected endpoints requiring authentication
- Role-based access control

### Data Security
- BCrypt password hashing
- User data isolation (users can only access their own data)
- Input validation to prevent injection attacks
- Secure error handling without exposing sensitive information

### Password Policy
- Minimum 8 characters
- Must contain at least one uppercase letter
- Must contain at least one lowercase letter
- Must contain at least one number
- Must contain at least one special character

## API Endpoints

### Authentication
- POST `/api/auth/register` - Register a new user
- POST `/api/auth/login` - Login and receive JWT token

### Blog Posts
- GET `/api/posts` - Get all posts
- GET `/api/posts/user/{userId}` - Get posts by user ID
- POST `/api/posts` - Create a new post
- PUT `/api/posts/{id}` - Update a post
- DELETE `/api/posts/{id}` - Delete a post

## Database Schema

The application uses two main tables:

### Users Table
- id (Primary Key)
- username
- email
- password (BCrypt hashed)
- role
- active
- created_at
- updated_at

### Blog Posts Table
- id (Primary Key)
- title
- content
- user_id (Foreign Key)
- created_at
- updated_at

## Getting Started

1. Clone the repository
2. Configure application.properties with your database settings
3. Run the application using Maven: `mvn spring-boot:run`
4. Access the API at `http://localhost:8080`

## Running Tests

```bash
mvn test
```

## Technologies Used

- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- JWT
- PostgreSQL
- Flyway
- Maven
- JUnit 5
- Mockito
