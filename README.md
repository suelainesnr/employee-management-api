# 👩‍💼 Employee Management API

## 📌 Overview

REST API for managing employees, built with Java and Spring Boot.
This project demonstrates backend development best practices, including layered architecture and CRUD operations.

## 🚀 Technologies

* Java 17
* Spring Boot
* Spring Data JPA
* PostgreSQL (or H2)
* Docker
* Swagger (OpenAPI)

## 🧠 Features

* Create employee
* List all employees
* Update employee data
* Delete employee

## 🏗️ Architecture

The project follows a layered architecture:

* Controller → Handles HTTP requests
* Service → Business rules
* Repository → Data access

## 🔗 API Endpoints

| Method | Endpoint        | Description         |
| ------ | --------------- | ------------------- |
| GET    | /employees      | List all employees  |
| GET    | /employees/{id} | Get employee by ID  |
| POST   | /employees      | Create new employee |
| PUT    | /employees/{id} | Update employee     |
| DELETE | /employees/{id} | Delete employee     |

## ▶️ Running the project

```bash
# Clone the repository
git clone https://github.com/seuusuario/employee-management-api.git

# Run with Maven
./mvnw spring-boot:run
```

## 📄 Documentation

API documentation available via Swagger:

```
http://localhost:8080/swagger-ui.html
```

## 📬 Contact

Developed by Suelaine Ramos
📧 [seuemail@gmail.com](mailto:seuemail@gmail.com)


