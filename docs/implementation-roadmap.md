
Now we need to make one final planning decision before implementation: **the order in which we'll build the system.**

# **Task 10 — Define our implementation roadmap**

We will not build 15 services independently and leave them disconnected. We'll build the system in **phases**, with each phase producing something working and testable.

## **10.1 Development phases**

## **Phase 1 — Development Foundation**

First

- Java 17 + Maven project standards
- Docker Desktop verification
- MySQL, Redis, Kafka and RabbitMQ
- Local configuration and environment variables
- Database migration standards using Flyway
- Git repository setup
  ## **Phase 2 — Core Business Services**
- Customer Service
- Product Catalogue & Pricing Service
- Eligibility Service
- Inventory Service
- Payment Service
  ## **Phase 3 — Order Management**
- Order Service
- Order lifecycle and state transitions
- Idempotent order creation
- Order history
- Price snapshots
- Transactional Outbox
  ## **Phase 4 — Order Processing**
- Order Orchestrator
- RabbitMQ commands and results
- Saga persistence
- Retry and timeout handling
- Compensation and recovery
- Kafka business events
  ## **Phase 5 — Telecom Fulfillment**
- Fulfillment and installation
- Provisioning
- Subscription
- Billing
- Notifications
  ## **Phase 6 — Client Access & Security**
- Identity Service
- API Gateway
- JWT authentication and authorization
- Rate limiting
- API documentation
  ## **Phase 7 — Production Engineering**
- Automated tests
- Testcontainers
- Observability and tracing
- Metrics and dashboards
- Load testing
- Resilience testing
- Dockerize the services

## **10.2 How we will develop each service**

Every service will follow a consistent development process.

1

Define business responsibility and APIs

2

Create Spring Boot project and dependencies

3

Configure application and database

4

Create Flyway schema migrations

5

Implement entities and repository

6

Implement business logic and validation

7

Implement REST APIs

8

Add exception handling and logging

9

Write unit and integration tests

10

Test with Postman

11

Integrate with other services when needed

We won't skip directly to Kafka or Saga just because they are important interview topics. First, we'll build reliable business functionality. Then we'll introduce distributed messaging when there is a real need for it.

## **10.3 Our first working business milestone**

Our first major milestone will be a working **product catalogue**, not the entire OMS.

We will be able to:

- Create telecom products.
- Define broadband, mobile and device product types.
- Store product specifications.
- Retrieve product details.
- Search/filter products.
- Validate product data.
- Store data in MySQL through Flyway-managed schemas.
- Test endpoints through Postman.
- Write tests for the business logic.

Once this works, we'll build the next service and gradually connect the business flow.

We will eventually demonstrate the complete workflow:

**Customer → Check availability → Select product → Checkout → Create order → Reserve inventory → Payment → Fulfillment → Activation → Subscription → Billing → Completion**

And we'll test both successful and failure scenarios.

