🚀 Tech Stack Overview

Frontend

  Vite + React – Modern, fast, and modular UI framework
  Axios – For API communication between frontend and backend

Backend

  Spring Boot Microservices:
  Gateway Service – Entry point handling routing and authentication
  User Service – Manages user registration, login, and profile data
  Activity Service – Tracks user activities and publishes events to Kafka
  AI Service – Consumes Kafka events and integrates with Google Gemini APIs for AI-based recommendations
  Apache Kafka – Enables asynchronous communication between microservices
  Keycloak – Manages authentication and authorization
  Eureka Server – Handles service discovery
  Spring Cloud Config Server – Provides centralized configuration management

🔄 System Workflow

  The frontend (or Postman) communicates with backend services through the Gateway.
  The Gateway routes requests to UserService or ActivityService based on API endpoints.
  ActivityService sends activity data to Kafka, which is consumed by AIService.
  AIService processes the data with Google Gemini APIs to generate personalized recommendations.
  Keycloak, Eureka, and ConfigServer provide security, service registration, and centralized configuration respectively.

🧩 Features

  Microservices architecture built with Spring Boot
  AI-powered fitness insights via Google Gemini
  Secure authentication with Keycloak
  Event-driven data processing with Kafka
  Centralized configuration and service discovery
  Scalable and modular design
