Before we start creating Spring Boot services, we need to design the most important business process in the entire system: **what happens when a customer places an order, and how the system handles failures along the way.**

# **Task 7 — Design the Order Lifecycle**

We'll use a broadband order with a router and installation as our primary workflow. Later, we'll adapt the same orchestration principles for SIM-only and handset orders.

## **7.1 Order lifecycle vs. Saga lifecycle**

These are two different things, and it's important to understand the difference.

- **Order lifecycle:** The business status visible to the customer.
- **Saga lifecycle:** The internal progress of the distributed workflow responsible for fulfilling the order.

For example, an order can be `PROCESSING` while its Saga is currently waiting for a payment response.

## **7.2 Customer-facing order states**

1\.

**PENDING**

2\.

**CONFIRMED**

3\.

**PROCESSING**

4\.

**FULFILLMENT_IN_PROGRESS**

5\.

**ACTIVATION_PENDING**

6\.

**COMPLETED**

Additional terminal or exception states:

| Status      | Meaning                                                  |
| ----------- | -------------------------------------------------------- |
| `CANCELLED` | Customer or system cancelled the order before completion |
| `FAILED`    | Order could not be fulfilled and failure was finalized   |
| `ON_HOLD`   | Order requires manual review or a business decision      |

We won't transition statuses arbitrarily. The Order Service will enforce allowed transitions.

## **7.3 Our primary Saga — broadband with router**

Here's the intended successful workflow:

1\. Checkout accepted & order created

2\. Saga started and state persisted

3\. Reserve router inventory

4\. Authorize payment

5\. Capture payment (business-policy dependent)

6\. Book installation appointment

7\. Dispatch router

8\. Provision broadband service

9\. Verify network activation

10\. Create subscription

11\. Initialize billing

12\. Mark order COMPLETED

13\. Notify customer

### **Why these steps are ordered this way**

- **Inventory before payment:** We don't want to charge the customer when equipment cannot be reserved.
- **Authorization before capture:** Depending on business rules, payment may be authorized first and captured at a later milestone.
- **Installation before activation:** Broadband generally needs the installation process completed before network provisioning.
- **Activation verification:** A provisioning request being accepted doesn't mean the network service is active.
- **Subscription & billing:** These must be initialized from confirmed business outcomes, not simply from order creation.
- **Completion:** The order becomes completed only after all mandatory fulfillment steps succeed.

These rules will be refined as we define the specific telecom scenarios. We won't assume every product follows the same workflow.

## **7.4 Failure and compensation scenarios**

This is where the project becomes a realistic distributed system.

| Failure scenario             | Expected system behavior                                                                                   |
| ---------------------------- | ---------------------------------------------------------------------------------------------------------- |
| Router reservation fails     | Stop workflow; fail order without payment                                                                  |
| Payment authorization fails  | Release router reservation; fail order                                                                     |
| Payment capture fails        | Investigate payment state; don't assume failure if outcome unknown                                         |
| Installation booking fails   | Retry if recoverable; otherwise void authorization/refund according to payment state and release resources |
| Router dispatch fails        | Retry fulfillment or hold for operational recovery                                                         |
| Provisioning fails           | Retry activation; if unrecoverable, cancel or compensate according to business policy                      |
| Subscription creation fails  | Retry safely; do not duplicate subscription                                                                |
| Billing initialization fails | Retry safely; order may remain pending completion                                                          |
| Notification fails           | Retry notification independently; don't reverse a successful order                                         |

**Compensation is not always a simple reverse operation.** For example, captured payment requires a refund, whereas an uncaptured authorization may be voided. Delivered equipment may require a return process rather than simply reversing delivery.

## **7.5 Timeout is not the same as failure**

Suppose Payment Service sends a capture request to a payment provider.

The provider captures the money, but the response never reaches our Payment Service because of a network timeout.

Our system sees a timeout.

It does **not** know whether the payment was captured or failed.

Therefore:

1. Persist the payment attempt.
2. Use a unique idempotency key.
3. If the response is unknown, mark the payment outcome as `UNKNOWN`.
4. Don't blindly retry capture using a new request.
5. Reconcile with the payment provider using the original transaction reference.
6. Resume or compensate the Saga only after the outcome is known.

This is a critical real-world payment design.

