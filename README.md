# SportsScore Live

An event-driven live-score platform that publishes score changes through Kafka and delivers them to browsers in real time with reactive Server-Sent Events (SSE).

![Architecture](https://img.shields.io/badge/architecture-event--driven-36d399) ![Java](https://img.shields.io/badge/Java-21-f89820) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4-6db33f)

## What it demonstrates

- Kafka-backed asynchronous messaging between independently deployable services.
- Reactive WebFlux SSE fan-out to connected browser clients.
- PostgreSQL persistence and Redis-backed reads in the Match Service.
- Request validation and structured API errors.
- Integration tests with real PostgreSQL and Kafka containers via Testcontainers.
- A browser-based score simulator that exercises the complete pipeline.

## Architecture

```text
Dashboard ──REST──> Match Service ──> PostgreSQL
    │                   │                 │
    │                   └── ScoreUpdateEvent ──> Kafka
    │                                                │
    └──────────── SSE <── Notification Service <─────┘
                         (Spring WebFlux)
```

`Match Service` owns match state. When a score changes, it persists the update, invalidates the match-list cache, and emits a Kafka event keyed by match ID. `Notification Service` consumes that event and broadcasts it to browser subscribers through an SSE stream.

## Run locally

The quickest route is Docker Compose. It starts PostgreSQL, Redis, Kafka, both services, and the dashboard:

```bash
docker compose up --build
```

Then open [http://localhost:8080](http://localhost:8080). Create a match and submit scores from the built-in simulator; updates should appear instantly on the live scoreboard.

Useful endpoints:

- Dashboard: `http://localhost:8080`
- Match API and Swagger UI: `http://localhost:8081/swagger-ui/index.html`
- Notification SSE stream: `http://localhost:8082/api/notifications/stream`

To run services outside Docker, start infrastructure with `docker compose up -d postgres redis zookeeper kafka`, then run `./gradlew bootRun` from each service directory.

## API examples

Create a match:

```bash
curl -X POST http://localhost:8081/api/matches \
  -H "Content-Type: application/json" \
  -d '{"homeTeam":"Melbourne United","awayTeam":"Sydney Kings"}'
```

Update its score (which produces the live event):

```bash
curl -X PUT http://localhost:8081/api/matches/1 \
  -H "Content-Type: application/json" \
  -d '{"homeScore":92,"awayScore":88}'
```

## Testing

```bash
cd match-service && ./gradlew test
cd ../notification-service && ./gradlew test
```

The Match Service suite validates database persistence and Kafka event publication against Testcontainers. The Notification Service suite includes stream broadcast coverage.

## Engineering decisions and next steps

Kafka decouples score writes from client delivery and provides a scalable event backbone; SSE is a simple, efficient one-way transport for browser score updates. Redis reduces repeat reads of the match list.

This demo intentionally uses an in-memory notification sink, which is suitable for a single Notification Service instance. A production deployment should use a shared fan-out strategy across replicas. Likewise, reliable publication across a database write and Kafka requires the transactional-outbox pattern with retries and idempotent consumers; those are the next resilience improvements.

## Stack

Java 21 · Spring Boot 4 · Spring MVC · Spring WebFlux · Apache Kafka · PostgreSQL · Redis · Docker Compose · Testcontainers · OpenAPI
