## **4.1 What will our company sell?**

[Huawei HG8245W5 Dual-Band Gigabit Epon/Gpon Router (Renewed) with Universal Plug - High-Speed Fiber Optic Access ONT | Daraz.pk](https://images.openai.com/static-rsc-4/kQmL816kgpJIJpkQ78rp7IZxs8KiadtXBTDSPbgH5uKKP_kCx2GS6AeJ4GB5ja8RGooD0ZDYNaqsui4Z2Aot9tY_iI8I9HzXy8jIkhgeiThJnyltFcn0fSKVX6izh0yAw38S1sQvcDQpsY5u7hscDplMZB4eZksfa1dBtXomi3U?purpose=inline)

**Fixed Broadband**

Recurring subscription

Fibre broadband packages, speeds, routers, installation appointments, network activation.

[Superdrug Mobile SIM card | Mobile SIM Card | Superdrug](https://images.openai.com/static-rsc-4/6V6Y_TRwxCtR5hUA5yiJbeHEinNQy8p4EZNGGAfD2odyEJw6spqB9JSH9oqzIJCGOm3rezDDtAvzVsaYn8Bz0G2eIPFGpPsw-9chOmFjxgoboT_rzc99wcXG8vwLZrLGo-cE4XvDWqlDolqpTAPCs-kEL05vbRU6nSa0MWsUbl4?purpose=inline)

**SIM-only Mobile Plans**

Recurring subscription

SIM cards, mobile plans, mobile numbers, activation, upgrades and renewals.

[Apple dẫn đầu tốc độ tăng trưởng 9%, Samsung giữ vững ngôi vương](https://images.openai.com/static-rsc-4/aDqTYUuHzhVuHJdX0oca-CQUEwPAS30uNLNnPJYcskzTTiR7tg7x9udo2lUMUckr_9GY88n2qUep5Nx0siT4YdB4t7fznO1jZ1xQA3nfG_AnrWViiWfMSG8T93iu3jMY7x9gH0NjLTfs1imnttxdWzSyTSE3a6HMfU7nUHUpqd4?purpose=inline)

**Mobile Devices**

One-time purchase

Handsets, device stock, delivery, device financing as a potential future capability.

[iPhone 11 Premium Refurbished phone & 12 month SIM - Boost Mobile](https://images.openai.com/static-rsc-4/48Z-0K-OP9fL4YHbryAhc1xpz75Iculw5Eu0bq9lUO5Jnk-bXouwnhshovDzrfFYH4-bktuLWBA2z0ZaSoYQmOnnXh3EGi6-Tna_IRIJyRhk3sQPeUvKpSLwO-fnoRzKXYjBVyqqsqqmd6mrWM1SsXR2VzKcDoEvFQGPaTsrDqs?purpose=inline)

**Mobile Bundles**

Combined purchase

Handset + SIM + mobile subscription, potentially with discounts.

## **4.2 What business workflows will the system support?**

These are the real business workflows our backend will eventually handle.

### **Workflow A — New broadband order**

1

Customer enters service address

2

Check broadband availability at that address

3

Select broadband plan and optional router

4

Validate eligibility and calculate price

5

Create order

6

Reserve router inventory

7

Process payment

8

Schedule installation

9

Deliver equipment / technician visit

10

Provision broadband service

11

Activate subscription and billing

12

Notify customer

### **Workflow B — SIM-only mobile order**

1. Customer selects a mobile plan.
2. System validates the plan and customer eligibility.
3. Allocate a SIM and mobile number, where applicable.
4. Create the order.
5. Process payment according to the plan's payment rules.
6. Activate the SIM through the provisioning capability.
7. Create the customer's mobile subscription.
8. Start recurring billing.
9. Send activation notification.

### **Workflow C — Mobile handset + SIM bundle**

1. Customer selects a handset and mobile plan.
2. System validates bundle pricing and compatibility.
3. Reserve handset stock.
4. Allocate SIM/mobile number.
5. Create the order.
6. Process payment.
7. Fulfill and deliver the handset.
8. Activate mobile service.
9. Create subscription and billing records.

### **Workflow D — Existing customer upgrade**

An existing customer should be able to upgrade their current service.

For example:

- Broadband customer upgrades from 100 Mbps to 500 Mbps.
- Mobile customer upgrades their plan.
- Customer upgrades handset while retaining their mobile number.
- Customer adds an additional SIM or service.

This introduces real business logic around existing subscriptions, eligibility, contract terms, prorated charges and service changes.

## **4.3 Scope of our project**

We will eventually support these capabilities:

| Capability          | What it does                                                  |
| ------------------- | ------------------------------------------------------------- |
| Customer & Account  | Customer profiles, addresses, account details                 |
| Identity & Security | Login, JWT, roles, authorization                              |
| Product Catalogue   | Broadband, SIMs, handsets, routers and product specifications |
| Offer & Pricing     | Plans, discounts, recurring and one-time charges              |
| Eligibility         | Address validation and service availability                   |
| Cart & Checkout     | Customer selections, validation, checkout                     |
| Order Management    | Orders, order items, status and history                       |
| Order Orchestration | Coordinates multi-service workflows and recovery              |
| Inventory           | Stock management and reservations                             |
| Payment             | Payment authorization, capture, refunds and reconciliation    |
| Fulfillment         | Shipment, dispatch and delivery                               |
| Installation        | Technician appointments and installation scheduling           |
| Provisioning        | Broadband and SIM network activation                          |
| Subscription        | Ongoing customer services and lifecycle                       |
| Billing             | Invoices, recurring charges and adjustments                   |
| Notification        | Email/SMS order updates and activation notifications          |

These are the business capabilities we are targeting. We will decide later which ones deserve independent microservices and which should initially be combined. We won't create 17 services just for the sake of calling the project microservices.

## **4.4 What we are deliberately not building initially**

- A marketplace with multiple sellers.
- A real telecom network.
- Real payment gateway transactions.
- Actual carrier SIM activation.
- Real courier integrations.
- Real technician dispatch integrations.

We will simulate external systems through well-designed interfaces and realistic responses, so our backend workflow behaves like a production system without requiring commercial integrations.

Excellent. We've now established the business domain and scope. This is important because our technical architecture will be driven by these business workflows—not by randomly creating Spring Boot applications.

Next, we define the **architecture style and service boundaries**.

# **Task 5 — Decide our architecture style**

## **5.1 Architecture we're going to use**

We will use a **domain-oriented microservices architecture with event-driven communication and Saga orchestration**.

This means our application will not be one giant Spring Boot application where every business function is mixed together.

Instead, business capabilities will have clear ownership, their own data, and defined communication contracts.

**Client Applications**

Web application · Mobile application · Postman

**API Gateway**

Routing · Authentication enforcement · Rate limiting · Request correlation

**Business Services**

Independent business capabilities and ownership

**Customer & Identity**

Accounts · Authentication

**Commerce**

Catalogue · Offers · Eligibility · Cart

**Order Domain**

Orders · Orchestration

**Operations**

Inventory · Payment · Fulfillment · Installation

**Service Lifecycle**

Provisioning · Subscription · Billing

**Notifications**

Email · SMS · Delivery tracking

**Communication & Infrastructure**

REST · Kafka · RabbitMQ · Redis

MySQL · Docker · Observability · Resilience

This is our conceptual architecture. It is not yet a finalized deployment diagram. We will refine the actual number of services as we define ownership and workflow boundaries.

## **5.2 How services will communicate**

We will use three main communication patterns. Each solves a different problem.

1

**REST API — Synchronous**

I need an answer now.

Example: Checkout asks Eligibility Service whether broadband is available at a customer's address. It needs the decision before proceeding.

Technology: Spring REST APIs, OpenFeign where appropriate

2

**Kafka — Event-driven**

Something has happened. Interested services can react.

Example: Order Service publishes `OrderCreated`. Analytics, notification, or other interested services can consume the event independently.

Technology: Kafka · Transactional Outbox · Idempotent consumers

3

**RabbitMQ — Command / Work queue**

Please perform this task.

Example: Orchestrator sends `ReserveInventory` command to Inventory Service. The worker processes it and reports the result.

Technology: RabbitMQ · Acknowledgements · Retry · Dead-letter queues

### **The difference with a real example**

Imagine a customer orders a handset.

- **REST:** Checkout asks the Pricing Service, "What is the current price of this handset and plan?"
- **RabbitMQ:** The workflow sends Inventory, "Reserve one handset for this order."
- **Kafka:** Order Service announces, "Order was created." Other consumers react to this fact.

We will not use Kafka and RabbitMQ for every single communication. Adding messaging unnecessarily makes systems more complicated. Each communication will have a reason.

## **5.3 Data ownership — a critical microservices rule**

Each service owns its own database and data.

For example:

| Service           | Owns                                                                |
| ----------------- | ------------------------------------------------------------------- |
| Product Catalogue | Product definitions and specifications                              |
| Inventory         | Available stock, reservations, allocated stock                      |
| Order             | Order records, order items, order status, purchased-price snapshots |
| Payment           | Payment transactions, authorization, capture, refund status         |
| Subscription      | Active telecom services and their lifecycle                         |
| Billing           | Invoices, billing periods, charge records, adjustments              |

If Order Service needs inventory information, it must use Inventory's API or a suitable event-driven integration.

**Order Service must not directly query Inventory Service's database.**

For local development, we may run one MySQL server with several databases. The separation is about ownership, access and responsibility—not necessarily separate physical MySQL servers.

## **5.4 What happens when something fails?**

A production system must be designed for failure—not just successful requests.

Consider this order:

1. Order is created.
2. Router inventory is reserved successfully.
3. Payment is captured successfully.
4. Installation scheduling fails.

We cannot simply return an HTTP 500 and forget everything that already happened.

Our architecture will account for:

| Problem                                | Solution                                             |
| -------------------------------------- | ---------------------------------------------------- |
| Service temporarily unavailable        | Timeout + bounded retry                              |
| Repeated failures                      | Circuit breaker                                      |
| Duplicate checkout request             | Idempotency key + database uniqueness                |
| Event published but service crashes    | Transactional Outbox                                 |
| Event delivered more than once         | Idempotent consumer / Inbox                          |
| Inventory reserved but order fails     | Compensation / reservation release                   |
| Payment outcome unknown due to timeout | Payment reconciliation, not blind retry              |
| Message processing repeatedly fails    | Dead-letter queue + recovery process                 |
| Order workflow stuck                   | Persisted Saga state + timeout monitoring + recovery |

We will implement these mechanisms progressively, with test cases demonstrating why they exist.

## **5.5 Order Orchestration — the heart of our project**

For our complex telecom workflows, we will use **Saga orchestration** rather than relying on a chain of REST calls inside one request.

The Orchestrator will maintain durable workflow state. It will know:

- Which order is being processed.
- Which workflow steps have completed.
- Which step is currently running.
- Whether a step needs retry.
- Whether the workflow has failed.
- Whether compensation is required.
- Whether manual intervention is needed.

It will not rely on a Java method remaining alive for the entire order lifecycle.

For example:

1

Order accepted

2

Inventory reservation

3

Payment processing

4

Installation / delivery

5

Network provisioning

6

Order completion

If a step fails, the orchestrator evaluates the business rules and the current workflow state to decide whether to retry, compensate, pause, or request intervention.

We will build and test this mechanism ourselves, so you'll understand how real workflow orchestration works.


Now comes an important decision: **we shouldn't create a separate microservice for every capability just because we can.**

In real production systems, too many small services create deployment overhead, network failures, operational complexity, and debugging difficulties.

# **Task 6 — Finalize our initial microservice boundaries**

We'll divide the business into services based on **business ownership, data ownership, and independent responsibilities**.

## **6.1 Our initial services**

These are the services I recommend we actually build for this project.

## **Platform & Commerce**

01

**API Gateway**

Entry point for client requests

02

**Identity Service**

Authentication and authorization

03

**Customer Service**

Customer profiles, accounts and addresses

04

**Catalogue & Pricing**

Products, plans, offers and pricing

05

**Eligibility Service**

Serviceability and eligibility decisions

06

**Cart & Checkout**

Cart, validation and checkout process

## **Order & Operations**

07

**Order Service**

Commercial order and lifecycle

08

**Order Orchestrator**

Saga coordination and recovery

09

**Inventory Service**

Stock and reservations

10

**Payment Service**

Payments, refunds and reconciliation

11

**Fulfillment & Installation**

Delivery and appointment coordination

## **Service Lifecycle**

12

**Provisioning Service**

Network / SIM activation

13

**Subscription Service**

Ongoing telecom services

14

**Billing Service**

Charges, invoices and adjustments

15

**Notification Service**

Email, SMS and notification delivery

This gives us **15 independently deployable services** as our target implementation, with a few deliberately combined business capabilities to keep the initial system manageable.

## **6.2 Why we combine some capabilities initially**

### **Catalogue + Pricing → one service**

Initially, we'll build `product-catalogue-service` to own:

- Product definitions — broadband plans, SIMs, devices, routers.
- Product specifications.
- Offer definitions and eligibility rules for offers.
- Pricing — monthly recurring charges and one-time charges.
- Discounts and bundle pricing.

Why combine them initially? Catalogue and Pricing are closely related during product selection and quotation. We can establish clear internal modules and ownership while avoiding extra network calls at the start.

Later, if pricing becomes complex, needs independent scaling, or has separate release requirements, we can extract `offer-pricing-service`.

### **Fulfillment + Installation → one service initially**

Initially, `fulfillment-service` will manage:

- Device/router shipment.
- Dispatch and delivery tracking.
- Installation appointment slots.
- Appointment booking, rescheduling and cancellation.
- Installation completion status.

Why combine them initially? They are both operational activities tied to getting the customer's purchased service delivered and installed. We will keep separate internal modules so that installation scheduling can be extracted into `installation-service` later.

### **Subscription and Billing remain separate**

We will **not** combine these.

- Subscription owns the customer's active telecom services and their lifecycle.
- Billing owns charges, invoices, billing cycles, adjustments and amounts owed.

They have different business rules, data ownership, and financial responsibilities.

## **6.3 Service ownership matrix**

| Service                    | Owns its business data                                                   |
| -------------------------- | ------------------------------------------------------------------------ |
| API Gateway                | Gateway configuration, routing and rate-limit configuration              |
| Identity                   | Credentials, identity records, roles and token lifecycle                 |
| Customer                   | Customer, account, address, contact and preferences                      |
| Catalogue & Pricing        | Products, offers, prices, discounts, price versions                      |
| Eligibility                | Serviceability checks, decisions, expiry and references                  |
| Cart & Checkout            | Cart state, checkout attempts and validation                             |
| Order                      | Orders, order items, price snapshots, order lifecycle                    |
| Orchestrator               | Saga/workflow state, step status, retries and compensation               |
| Inventory                  | Stock, locations, reservation and allocation records                     |
| Payment                    | Payment intents, transaction references, authorization, capture, refunds |
| Fulfillment & Installation | Shipment, tracking, installation appointments                            |
| Provisioning               | Activation requests, external responses, activation status               |
| Subscription               | Active services, service lifecycle, contract information                 |
| Billing                    | Billing accounts, invoices, charges, adjustments                         |
| Notification               | Notification requests, delivery status, templates and retry state        |

Each service will own its database. Other services will interact with it through APIs or events—not direct database access.

## **6.4 Naming conventions**

We'll use consistent names throughout our repository:

| Service                    | Spring Boot project name     |
| -------------------------- | ---------------------------- |
| API Gateway                | `api-gateway`                |
| Identity                   | `identity-service`           |
| Customer                   | `customer-service`           |
| Catalogue & Pricing        | `product-catalogue-service`  |
| Eligibility                | `eligibility-service`        |
| Cart & Checkout            | `cart-checkout-service`      |
| Order                      | `order-service`              |
| Orchestrator               | `order-orchestrator-service` |
| Inventory                  | `inventory-service`          |
| Payment                    | `payment-service`            |
| Fulfillment & Installation | `fulfillment-service`        |
| Provisioning               | `provisioning-service`       |
| Subscription               | `subscription-service`       |
| Billing                    | `billing-service`            |
| Notification               | `notification-service`       |

We won't create all these projects now. We'll first establish the architecture and then create services in a logical implementation order, testing each one before proceeding.



