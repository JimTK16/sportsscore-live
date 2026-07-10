# 🏀 SportsScore Live

A real-time sports score notification system built with a **microservices** and **event-driven architecture**.

SportsScore Live broadcasts live sports score updates to thousands of concurrent users with minimal latency. The application is built using **Spring Boot 4.x**, **Apache Kafka**, **Redis**, and **Spring WebFlux** to deliver scalable, real-time notifications through **Server-Sent Events (SSE)**.

---

# 🏗️ Architecture

## Data Flow

```text
                  +---------------------+
                  |     Client API      |
                  +----------+----------+
                             |
                             v
                   +-------------------+
                   |   Match Service   |
                   |-------------------|
                   | Spring Boot       |
                   | PostgreSQL        |
                   | Redis Cache       |
                   +---------+---------+
                             |
                  Publish ScoreUpdateEvent
                             |
                             v
                   +-------------------+
                   |   Apache Kafka    |
                   +---------+---------+
                             |
                    Consume Events
                             |
                             v
              +-----------------------------+
              | Notification Service        |
              | Spring WebFlux + SSE        |
              +-------------+---------------+
                            |
                  Server-Sent Events (SSE)
                            |
                            v
                    +---------------+
                    | Web Browser   |
                    | index.html    |
                    +---------------+
```

### Match Service (Producer)

The Match Service exposes REST APIs for creating matches and updating scores.

When a score changes, it:

- Saves the updated match to PostgreSQL
- Invalidates the Redis cache
- Publishes a `ScoreUpdateEvent` JSON message to Kafka

### Apache Kafka (Event Broker)

Kafka acts as the messaging backbone between services by:

- Decoupling producers and consumers
- Providing reliable event delivery
- Supporting high-throughput event processing

### Notification Service (Consumer)

The Notification Service:

- Consumes score update events from Kafka
- Streams updates to connected browsers using **Server-Sent Events (SSE)**
- Uses Spring WebFlux for fully reactive, non-blocking communication

---

# 💻 Tech Stack

| Category           | Technology                       |
| ------------------ | -------------------------------- |
| Language           | Java 21                          |
| Framework          | Spring Boot 4.0.6                |
| Reactive Streaming | Spring WebFlux (Project Reactor) |
| Messaging          | Apache Kafka                     |
| Database           | PostgreSQL                       |
| Cache              | Redis                            |
| Testing            | JUnit 5, Testcontainers          |
| Infrastructure     | Docker Compose                   |
| CI/CD              | GitHub Actions                   |
| API Documentation  | Swagger / OpenAPI 3              |

---

# 🚀 Quick Start

## Prerequisites

- Java 21
- Docker
- Docker Compose

---

## 1. Start Infrastructure

Launch Kafka, Zookeeper, PostgreSQL, and Redis.

```bash
docker-compose up -d
```

---

## 2. Start the Microservices

### Terminal 1 – Match Service

```bash
cd match-service
./gradlew bootRun
```

### Terminal 2 – Notification Service

```bash
cd notification-service
./mvnw spring-boot:run
```

> **Note:** Depending on how each service was generated, use either `./gradlew` or `./mvnw`.

---

## 3. Test Real-Time Updates

1. Open the included `index.html` using the **Live Server** extension in VS Code.
2. Open Swagger UI:

```
http://localhost:8081/swagger-ui.html
```

3. Call the following endpoint:

```
PUT /api/matches/{id}/score
```

4. Watch the connected browser receive live score updates instantly via **Server-Sent Events (SSE)** without refreshing the page.

---

# 🧪 Testing

Integration tests are powered by **Testcontainers**.

The test suite automatically starts temporary Docker containers for:

- PostgreSQL
- Apache Kafka

This enables end-to-end verification of:

- Database persistence
- Kafka message publishing
- Spring Boot integration

Run the tests:

```bash
cd match-service
./gradlew test
```

---

# ✨ Key Features

- Event-driven microservices architecture
- Real-time score notifications via Server-Sent Events (SSE)
- Apache Kafka asynchronous messaging
- Redis caching for improved read performance
- PostgreSQL persistence
- Reactive programming with Spring WebFlux
- Integration testing with Testcontainers
- Docker Compose local development
- Swagger/OpenAPI documentation
- CI/CD ready with GitHub Actions
