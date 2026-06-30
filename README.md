# NeoBank - Digital Banking Backend System

A secure and scalable backend system built using Java and Spring Boot that simulates core banking operations such as account management and transactions.


---

## Features
- User registration and authentication using JWT
- Account creation and management
- Deposit and withdrawal APIs
- Transaction handling
- OTP-based verification system
- Role-based authorization

---

## Tech Stack
- Java
- Spring Boot
- PostgreSQL
- JWT Authentication
- Maven

---

## API Endpoints

POST /register  
Used to register a new user with required details.

POST /login  
Authenticates the user and returns a JWT token.

POST /deposit  
Allows the user to deposit money into their account.

POST /withdraw  
Allows the user to withdraw money from their account.

GET /balance  
Fetches the current account balance of the user.
---

## How to Run

1. Clone the repository  
2. Configure PostgreSQL in application.properties  
3. Run the Spring Boot application  
4. Use Postman or any API client to test the endpoints  

---

## Author

Abhishek Chandra  
Java Backend Developer  
