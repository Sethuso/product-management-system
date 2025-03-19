**Product Management System

This repository contains a Java-based Microservices Project for a Product Management System. The system is divided into multiple microservices, along with ServiceRegistry and API Gateway.

📂 Services Included

ProductService: Manages product-related functionalities (CRUD, fetching price & inventory via Feign).

InventoryService: Manages inventory for products.

PricingService: Manages pricing details for products.

UserService: Handles user management (Registration, Authentication, Roles).

ServiceRegistry: Enables service discovery for microservices.

API Gateway: Acts as a single entry point to route requests to appropriate services.

⚙️ Tech Stack

Backend: Java, Spring Boot, Spring Data JPA, OpenFeign

Database: MySQL

Testing: JUnit 5, Mockito

Documentation: Swagger

Others: Lombok, ModelMapper, Logback (Logging), Docker (Containerization)

📌 Prerequisites

Java 17+

Maven

MySQL

Git


📁 Folder Structure

Product-Management-System/  (Root Folder)
├── ProductService/
├── InventoryService/
├── PricingService/
├── UserService/
├── ServiceRegistry/
├── APIGateway/
├── .gitignore
└── README.md

**
