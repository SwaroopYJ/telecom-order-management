# **Task 9 — Design our local infrastructure**

We will develop and run the application locally first. We won't need AWS, an AWS account, or cloud resources at this stage.

## **9.1 Local environment architecture**

Our development environment will consist of two parts:

- **Spring Boot microservices:** Run from IntelliJ IDEA initially.
- **Infrastructure components:** Run in Docker containers using Docker Compose.

**Your Windows Machine**

IntelliJ IDEA · Postman · Docker Desktop

**Spring Boot Services — IntelliJ**

API Gateway

Customer Service

Order Service

Payment Service

Other Services

REST · Kafka · RabbitMQ · Database connections

**Docker Compose — Local Infrastructure**

**MySQL**

Persistent databases

**Redis**

Cache & temporary data

**Kafka**

Business events

**RabbitMQ**

Commands & work queues

This setup lets us develop, debug, and test each service directly in IntelliJ while the supporting infrastructure runs independently in containers.

Later, we'll containerize the Spring Boot applications too.

## **9.2 Technology responsibilities**

We will use each technology for a specific reason.

[Kanbel Technologies](https://images.openai.com/static-rsc-4/lIUH81tejlkswCXyNrWuAAxHsjybAQlaIuDKv-Z_XUmCzHcKcT0u90kraHfiEhBc5_c_lgT8bKGRksbnHzwIig_sBovMW8wilv9VZzv32K_9sTK0jrXJGhpYyCvO-HD-GtYa78cFp6aregV3pNY-Eylq1Zne85ayQKZjtQ3hhl4?purpose=inline)

**MySQL — Durable business data**

- Order records and order items.
- Inventory and reservations.
- Payment records and transaction history.
- Customer, subscription and billing data.
- Saga state and step execution records.

Source of truth for persistent business data.

[Redis Logo PNG Vector (SVG) Free Download](https://images.openai.com/static-rsc-4/epIZvTJqrn11fZoyLAql3XQvzvr05WTs4k_hS-9tdnMQyi9Nak_EpGx1d16Qeud3z3mmGKrPzkfB8l4M10kTA8OEXwDm6CtTKKvBDOawlkZbKJwF8Jjm3IiHXcBUIohDPkufesDQyxMCNKXpFH79YSUkxtDVtdaR-HKvPqdpBXw?purpose=inline)

**Redis — Fast access and temporary data**

- Product catalogue cache.
- Pricing cache with proper expiration/invalidation.
- Short-lived cart data where appropriate.
- Rate limiting.
- Temporary data with explicit TTL.

Not the source of truth for orders, payments, or inventory.

[Running Kafka on Production. Apache Kafka is an open-source… | by Pradyumna Khawas | WorkIndia.in | Medium](https://images.openai.com/static-rsc-4/IdgpctULbez35q_RTbMO09b-y6gGSwj0M361O82eRjmf4iacIuOlU2NJe7tJ5nG3ML_-Ramx8HPhw_f2BkJ4-7fTax9B_pG2F5mYYn7fI3Wkxu6lEMjmv6TQdRCI7p2vkYr-RROSNxhp8BCekHH4j-k8olCh620FHns5GrtDyDM?purpose=inline)

**Kafka — Business event streaming**

- `OrderCreated`
- `PaymentCaptured`
- `InventoryReserved`
- `BroadbandActivated`
- `SubscriptionCreated`

Multiple independent consumers can react to the same event. Events represent facts that have already happened.

[Unlocking the Potential of RabbitMQ Streams: A Game-Changer for Messaging Systems](https://images.openai.com/static-rsc-4/4sDTjOrncePEUamlK7jdB7IqBfZZ6ZKkfeayerJBfWDld8JxdbhkH9-6gWDz_coFO2uMQDbqmt3YjHluNqXI58H3EU9tpH2HxiTwIb107yMEfEYyPl815lIW6gGTcfgIlKa2iPTuiYSWbIq9HN-LKqWrvbleTeeMOGKGXN1KmsU?purpose=inline)

**RabbitMQ — Commands and asynchronous tasks**

- `ReserveInventory`
- `AuthorizePayment`
- `CapturePayment`
- `BookInstallation`
- `ActivateBroadband`

Worker services receive commands, execute the requested action and report the outcome.

### **Example of how Kafka and RabbitMQ work together**

Suppose the Orchestrator needs a router reserved for an order.

1. Orchestrator sends a `ReserveInventory` command through RabbitMQ.
2. Inventory Service receives and processes the command.
3. Inventory Service persists the reservation in MySQL.
4. It records its result reliably and publishes `InventoryReserved`.
5. The Orchestrator consumes the result/event and updates its Saga state.
6. The Orchestrator decides the next step: payment authorization.

We will design reliable publication using the **Transactional Outbox** pattern. We won't simply commit a database transaction and hope a message is published successfully afterward.

We will also implement duplicate-message protection using idempotent processing and appropriate database constraints.

## **9.3 Database isolation**

For local development, we can use one MySQL container with multiple separate databases.

For example:

| Database          | Owning service       |
| ----------------- | -------------------- |
| `identity_db`     | Identity Service     |
| `customer_db`     | Customer Service     |
| `catalogue_db`    | Catalogue & Pricing  |
| `eligibility_db`  | Eligibility Service  |
| `checkout_db`     | Cart & Checkout      |
| `order_db`        | Order Service        |
| `orchestrator_db` | Order Orchestrator   |
| `inventory_db`    | Inventory Service    |
| `payment_db`      | Payment Service      |
| `fulfillment_db`  | Fulfillment Service  |
| `provisioning_db` | Provisioning Service |
| `subscription_db` | Subscription Service |
| `billing_db`      | Billing Service      |
| `notification_db` | Notification Service |

We can use the same MySQL server to reduce local resource usage, but:

- Each service accesses only its own database.
- We will not create cross-service joins.
- Services will not share repositories or database entities.
- Schema changes will be managed by Flyway migrations owned by each service.
- In production, database isolation can be strengthened using separate instances or managed database resources where required.

## **9.4 Local infrastructure management**

We will use a single Docker Compose file initially:

```
infrastructure/
└── docker-compose.yml
```

It will manage:

- MySQL
- Redis
- Kafka
- RabbitMQ

We will use persistent Docker volumes for components where data should survive container restarts.

For example, restarting MySQL should not erase our orders and payment records. We will also learn how to intentionally reset local data when needed.

## **9.5 Local connectivity**

Services running in IntelliJ will connect to infrastructure using localhost and the exposed ports.

| Component              | Local connection                                     |
| ---------------------- | ---------------------------------------------------- |
| MySQL                  | `localhost:3306`                                     |
| Redis                  | `localhost:6379`                                     |
| Kafka                  | `localhost:9092` (subject to listener configuration) |
| RabbitMQ               | `localhost:5672`                                     |
| RabbitMQ Management UI | `localhost:15672`                                    |

Kafka requires special listener configuration because it must be accessible from both Docker containers and applications running on your Windows host. We will configure this correctly when we set up Kafka, rather than copying a generic configuration that may not work.

## **9.6 Security for local development**

Even locally, we'll try to follow good practices:

- Credentials stored in environment variables or local configuration—not committed to Git.
- Separate database credentials for each service as we progress.
- JWT secrets supplied through configuration.
- RabbitMQ and Kafka endpoints configurable per environment.
- No real customer personal data or real payment details.
- Local development credentials will be clearly identified as development-only.

Later, we'll introduce production configuration, external secret management and cloud-specific security without changing the core business logic unnecessarily.

