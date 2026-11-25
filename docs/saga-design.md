Now we need to make the Saga design more precise. Previously, we listed the steps, but we haven't defined **which service triggers each step, how the message travels, who stores the state, and how the Saga resumes after a crash**.

# **Task 8 — Design the Saga State Machine**

## **8.1 Why we need a state machine**

Our Saga must be a **durable workflow**, not just a Java method calling service after service.

Imagine our Orchestrator starts processing an order:

1. Inventory reservation succeeds.
2. Payment authorization succeeds.
3. The application crashes before it books installation.

When the application restarts, it must know:

- Which order was being processed.
- Which steps already succeeded.
- Which step was in progress.
- Whether it is safe to retry.
- Whether it needs to compensate for completed steps.

We achieve this by persisting the Saga's state in its own database.

## **8.2 Who owns what?**

| Component            | Responsibility                                                                  |
| -------------------- | ------------------------------------------------------------------------------- |
| Order Service        | Owns the commercial order and customer-visible order status                     |
| Order Orchestrator   | Owns Saga state, step progression, retries, timeouts and compensation decisions |
| Inventory Service    | Owns inventory and reservation records                                          |
| Payment Service      | Owns payment attempts and their financial states                                |
| Fulfillment Service  | Owns installation appointments, shipments and delivery                          |
| Provisioning Service | Owns activation requests and verified activation status                         |
| Subscription Service | Owns the active telecom subscription                                            |
| Billing Service      | Owns billing initialization, charges and invoices                               |

The Orchestrator does not directly change the Inventory, Payment or other service databases. It sends commands to those services.

## **8.3 Saga state progression**

The Saga will move through explicit states.

STARTED

INVENTORY_RESERVATION_PENDING

INVENTORY_RESERVED

PAYMENT_AUTHORIZATION_PENDING

PAYMENT_AUTHORIZED

PAYMENT_CAPTURE_PENDING

PAYMENT_CAPTURED

INSTALLATION_BOOKING_PENDING

INSTALLATION_BOOKED

FULFILLMENT_PENDING

FULFILLED

PROVISIONING_PENDING

SERVICE_ACTIVATED

SUBSCRIPTION_PENDING

SUBSCRIPTION_CREATED

BILLING_PENDING

BILLING_INITIALIZED

COMPLETED

This is our **happy-path state progression**. Real implementation will also support failure, timeout, retry, cancellation and compensation states.

### **Important distinction**

`PAYMENT_CAPTURED` means our payment workflow has successfully captured the payment. It does not mean the order is completed.

Similarly:

- `INVENTORY_RESERVED` does not mean the router has been shipped.
- `FULFILLED` does not necessarily mean the network service is activated.
- `SERVICE_ACTIVATED` does not mean the invoice has been initialized.

Each state represents a verified business milestone.

## **8.4 Who sends which command?**

The Orchestrator is responsible for advancing the workflow. It sends commands through RabbitMQ.

| Saga step           | Command sent by Orchestrator | Target service |
| ------------------- | ---------------------------- | -------------- |
| Reserve stock       | `ReserveInventory`           | Inventory      |
| Authorize payment   | `AuthorizePayment`           | Payment        |
| Capture payment     | `CapturePayment`             | Payment        |
| Book installation   | `BookInstallation`           | Fulfillment    |
| Dispatch equipment  | `DispatchEquipment`          | Fulfillment    |
| Activate service    | `ActivateBroadband`          | Provisioning   |
| Create subscription | `CreateSubscription`         | Subscription   |
| Initialize billing  | `InitializeBilling`          | Billing        |

Each command will have a unique `commandId`, `sagaId`, `orderId`, and relevant business payload.

The target service processes the command and sends a result. That result may be delivered as a message, and the Orchestrator updates its persisted Saga state before deciding what to do next.

## **8.5 Example: how the workflow actually moves**

Let's take `ReserveInventory` as an example.

**Order Orchestrator**

Persists state = INVENTORY_RESERVATION_PENDING

RabbitMQ — ReserveInventory command

**Inventory Service**

Checks stock · Creates reservation · Commits database transaction

Result message — InventoryReserved

**Order Orchestrator**

Persists state = INVENTORY_RESERVED

Next → AuthorizePayment

The important part is that Inventory doesn't directly call Payment. It reports the outcome, and the Orchestrator decides the next step.

The actual implementation will use durable messaging, transaction boundaries, duplicate handling and recovery. This diagram shows the logical interaction, not every infrastructure detail.

## **8.6 Failure states and recovery**

We will design the following Saga states for failures:

| State                  | Meaning                                                          |
| ---------------------- | ---------------------------------------------------------------- |
| `RETRY_PENDING`        | A recoverable operation failed and retry is scheduled            |
| `COMPENSATION_PENDING` | A previously completed operation must be reversed or compensated |
| `COMPENSATING`         | Compensation operations are currently being executed             |
| `FAILED`               | Workflow failure has been finalized                              |
| `MANUAL_REVIEW`        | System cannot safely determine the next action automatically     |
| `CANCELLED`            | Workflow cancellation has been completed                         |

Example: inventory reservation succeeds but payment authorization fails.

\#chatgpt-mermaid-\_r_1ho\_{font-family:-apple-system-body,ui-sans-serif,-apple-system,system-ui,"Segoe UI",Helvetica,"Apple Color Emoji",Arial,sans-serif,"Segoe UI Emoji","Segoe UI Symbol";font-size:16px;fill:rgb(237, 237, 237);}@keyframes edge-animation-frame{from{stroke-dashoffset:0;}}@keyframes dash{to{stroke-dashoffset:0;}}#chatgpt-mermaid-\_r_1ho\_ .edge-animation-slow{stroke-dasharray:9,5!important;stroke-dashoffset:900;animation:dash 50s linear infinite;stroke-linecap:round;}#chatgpt-mermaid-\_r_1ho\_ .edge-animation-fast{stroke-dasharray:9,5!important;stroke-dashoffset:900;animation:dash 20s linear infinite;stroke-linecap:round;}#chatgpt-mermaid-\_r_1ho\_ .error-icon{fill:rgb(48, 48, 48);}#chatgpt-mermaid-\_r_1ho\_ .error-text{fill:rgb(237, 237, 237);stroke:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_1ho\_ .edge-thickness-normal{stroke-width:1px;}#chatgpt-mermaid-\_r_1ho\_ .edge-thickness-thick{stroke-width:3.5px;}#chatgpt-mermaid-\_r_1ho\_ .edge-pattern-solid{stroke-dasharray:0;}#chatgpt-mermaid-\_r_1ho\_ .edge-thickness-invisible{stroke-width:0;fill:none;}#chatgpt-mermaid-\_r_1ho\_ .edge-pattern-dashed{stroke-dasharray:3;}#chatgpt-mermaid-\_r_1ho\_ .edge-pattern-dotted{stroke-dasharray:2;}#chatgpt-mermaid-\_r_1ho\_ .marker{fill:rgb(175, 175, 175);stroke:rgb(175, 175, 175);}#chatgpt-mermaid-\_r_1ho\_ .marker.cross{stroke:rgb(175, 175, 175);}#chatgpt-mermaid-\_r_1ho\_ svg{font-family:-apple-system-body,ui-sans-serif,-apple-system,system-ui,"Segoe UI",Helvetica,"Apple Color Emoji",Arial,sans-serif,"Segoe UI Emoji","Segoe UI Symbol";font-size:16px;}#chatgpt-mermaid-\_r_1ho\_ p{margin:0;}#chatgpt-mermaid-\_r_1ho\_ .label{font-family:-apple-system-body,ui-sans-serif,-apple-system,system-ui,"Segoe UI",Helvetica,"Apple Color Emoji",Arial,sans-serif,"Segoe UI Emoji","Segoe UI Symbol";color:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_1ho\_ .cluster-label text{fill:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_1ho\_ .cluster-label span{color:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_1ho\_ .cluster-label span p{background-color:transparent;}#chatgpt-mermaid-\_r_1ho\_ .label text,#chatgpt-mermaid-\_r_1ho\_ span{fill:rgb(237, 237, 237);color:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_1ho\_ .node rect,#chatgpt-mermaid-\_r_1ho\_ .node circle,#chatgpt-mermaid-\_r_1ho\_ .node ellipse,#chatgpt-mermaid-\_r_1ho\_ .node polygon,#chatgpt-mermaid-\_r_1ho\_ .node path{fill:rgb(9, 23, 44);stroke:rgb(31, 78, 148);stroke-width:1px;}#chatgpt-mermaid-\_r_1ho\_ .rough-node .label text,#chatgpt-mermaid-\_r_1ho\_ .node .label text,#chatgpt-mermaid-\_r_1ho\_ .image-shape .label,#chatgpt-mermaid-\_r_1ho\_ .icon-shape .label{text-anchor:middle;}#chatgpt-mermaid-\_r_1ho\_ .node .katex path{fill:#000;stroke:#000;stroke-width:1px;}#chatgpt-mermaid-\_r_1ho\_ .rough-node .label,#chatgpt-mermaid-\_r_1ho\_ .node .label,#chatgpt-mermaid-\_r_1ho\_ .image-shape .label,#chatgpt-mermaid-\_r_1ho\_ .icon-shape .label{text-align:center;}#chatgpt-mermaid-\_r_1ho\_ .node.clickable{cursor:pointer;}#chatgpt-mermaid-\_r_1ho\_ .root .anchor path{fill:rgb(175, 175, 175)!important;stroke-width:0;stroke:rgb(175, 175, 175);}#chatgpt-mermaid-\_r_1ho\_ .arrowheadPath{fill:rgb(175, 175, 175);}#chatgpt-mermaid-\_r_1ho\_ .edgePath .path{stroke:rgb(175, 175, 175);stroke-width:1px;}#chatgpt-mermaid-\_r_1ho\_ .flowchart-link{stroke:rgb(175, 175, 175);fill:none;}#chatgpt-mermaid-\_r_1ho\_ .edgeLabel{background-color:rgb(0, 0, 0);text-align:center;}#chatgpt-mermaid-\_r_1ho\_ .edgeLabel p{background-color:rgb(0, 0, 0);}#chatgpt-mermaid-\_r_1ho\_ .edgeLabel rect{opacity:0.5;background-color:rgb(0, 0, 0);fill:rgb(0, 0, 0);}#chatgpt-mermaid-\_r_1ho\_ .labelBkg{background-color:rgba(0, 0, 0, 0.5);}#chatgpt-mermaid-\_r_1ho\_ .cluster rect{fill:rgb(48, 48, 48);stroke:rgba(255, 255, 255, 0.15);stroke-width:1px;}#chatgpt-mermaid-\_r_1ho\_ .cluster text{fill:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_1ho\_ .cluster span{color:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_1ho\_ div.mermaidTooltip{position:absolute;text-align:center;max-width:200px;padding:2px;font-family:-apple-system-body,ui-sans-serif,-apple-system,system-ui,"Segoe UI",Helvetica,"Apple Color Emoji",Arial,sans-serif,"Segoe UI Emoji","Segoe UI Symbol";font-size:12px;background:rgb(48, 48, 48);border:1px solid rgba(255, 255, 255, 0.15);border-radius:2px;pointer-events:none;z-index:100;}#chatgpt-mermaid-\_r_1ho\_ .flowchartTitleText{text-anchor:middle;font-size:18px;fill:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_1ho\_ rect.text{fill:none;stroke-width:0;}#chatgpt-mermaid-\_r_1ho\_ .icon-shape,#chatgpt-mermaid-\_r_1ho\_ .image-shape{background-color:rgb(0, 0, 0);text-align:center;}#chatgpt-mermaid-\_r_1ho\_ .icon-shape p,#chatgpt-mermaid-\_r_1ho\_ .image-shape p{background-color:rgb(0, 0, 0);padding:2px;}#chatgpt-mermaid-\_r_1ho\_ .icon-shape .label rect,#chatgpt-mermaid-\_r_1ho\_ .image-shape .label rect{opacity:0.5;background-color:rgb(0, 0, 0);fill:rgb(0, 0, 0);}#chatgpt-mermaid-\_r_1ho\_ .label-icon{display:inline-block;height:1em;overflow:visible;vertical-align:-0.125em;}#chatgpt-mermaid-\_r_1ho\_ .node .label-icon path{fill:currentColor;stroke:revert;stroke-width:revert;}#chatgpt-mermaid-\_r_1ho\_ .node .neo-node{stroke:rgb(31, 78, 148);}#chatgpt-mermaid-\_r_1ho\_ [data-look="neo"].node rect,#chatgpt-mermaid-\_r_1ho\_ [data-look="neo"].cluster rect,#chatgpt-mermaid-\_r_1ho\_ [data-look="neo"].node polygon{stroke:url(#chatgpt-mermaid-\_r_1ho\_-gradient);filter:drop-shadow( 1px 2px 2px rgba(185,185,185,1));}#chatgpt-mermaid-\_r_1ho\_ [data-look="neo"].swimlane.cluster rect{filter:none;}#chatgpt-mermaid-\_r_1ho\_ [data-look="neo"].node path{stroke:url(#chatgpt-mermaid-\_r_1ho\_-gradient);stroke-width:1px;}#chatgpt-mermaid-\_r_1ho\_ [data-look="neo"].node .outer-path{filter:drop-shadow( 1px 2px 2px rgba(185,185,185,1));}#chatgpt-mermaid-\_r_1ho\_ [data-look="neo"].node .neo-line path{stroke:rgb(31, 78, 148);filter:none;}#chatgpt-mermaid-\_r_1ho\_ [data-look="neo"].node circle{stroke:url(#chatgpt-mermaid-\_r_1ho\_-gradient);filter:drop-shadow( 1px 2px 2px rgba(185,185,185,1));}#chatgpt-mermaid-\_r_1ho\_ [data-look="neo"].node circle .state-start{fill:#000000;}#chatgpt-mermaid-\_r_1ho\_ [data-look="neo"].icon-shape .icon{fill:url(#chatgpt-mermaid-\_r_1ho\_-gradient);filter:drop-shadow( 1px 2px 2px rgba(185,185,185,1));}#chatgpt-mermaid-\_r_1ho\_ [data-look="neo"].icon-shape .icon-neo path{stroke:url(#chatgpt-mermaid-\_r_1ho\_-gradient);filter:drop-shadow( 1px 2px 2px rgba(185,185,185,1));}#chatgpt-mermaid-\_r_1ho\_ .node text{font-size:14px;font-weight:600;letter-spacing:normal;fill:rgb(153, 206, 255);}#chatgpt-mermaid-\_r_1ho\_ .edgeLabels text{font-size:13px;font-weight:600;letter-spacing:-0.08px;fill:rgb(153, 206, 255);}#chatgpt-mermaid-\_r_1ho\_ .node tspan[font-weight="normal"],#chatgpt-mermaid-\_r_1ho\_ .edgeLabels tspan[font-weight="normal"]{font-weight:600;}#chatgpt-mermaid-\_r_1ho\_ .edgeLabel .label rect{opacity:1;rx:13px;ry:13px;fill:rgb(0, 14, 26);stroke:rgb(26, 62, 95);stroke-width:1px;}#chatgpt-mermaid-\_r_1ho\_ .node rect,#chatgpt-mermaid-\_r_1ho\_ .node circle,#chatgpt-mermaid-\_r_1ho\_ .node ellipse,#chatgpt-mermaid-\_r_1ho\_ .node polygon,#chatgpt-mermaid-\_r_1ho\_ .node path{fill:rgb(0, 40, 77);stroke:rgba(255, 255, 255, 0.1);stroke-width:1px;}#chatgpt-mermaid-\_r_1ho\_ .node rect{rx:16px;ry:16px;}#chatgpt-mermaid-\_r_1ho\_ .mermaid-decision .label-container{fill:rgb(0, 14, 26);stroke:rgb(26, 62, 95);stroke-dasharray:2,2;}#chatgpt-mermaid-\_r_1ho\_ .edgePaths .flowchart-link{stroke:rgb(175, 175, 175);stroke-width:1px;stroke-linecap:round;stroke-linejoin:round;}#chatgpt-mermaid-\_r_1ho\_ .marker{fill:rgb(175, 175, 175);stroke:rgb(175, 175, 175);}#chatgpt-mermaid-\_r_1ho\_ :root{--mermaid-font-family:-apple-system-body,ui-sans-serif,-apple-system,system-ui,"Segoe UI",Helvetica,"Apple Color Emoji",Arial,sans-serif,"Segoe UI Emoji","Segoe UI Symbol";}Saga StartedReserve InventoryReservation successful?Authorize PaymentFail OrderPayment authorized?Continue SagaRelease InventoryRelease successful?Fail OrderManual ReviewYesNoYesNoYesNo

If inventory release fails, we cannot pretend the reservation was released. The Saga must retain the unresolved compensation and recover it later.

## **8.7 What must be persisted?**

The Orchestrator will have its own database records containing information such as:

| Field                 | Purpose                                           |
| --------------------- | ------------------------------------------------- |
| `saga_id`             | Unique identifier for the workflow instance       |
| `order_id`            | Business order being processed                    |
| `saga_type`           | Broadband, SIM-only, handset bundle, etc.         |
| `current_state`       | Current workflow state                            |
| `status`              | Running, compensating, completed, failed, etc.    |
| `last_completed_step` | Last verified successful step                     |
| `retry_count`         | Retry tracking                                    |
| `next_retry_at`       | Scheduled retry time                              |
| `failure_reason`      | Reason for failure                                |
| `created_at`          | Saga creation timestamp                           |
| `updated_at`          | Last state update timestamp                       |
| `version`             | Optimistic locking / concurrent update protection |

We will also maintain a separate Saga-step execution history rather than relying only on `last_completed_step`. That will let us audit what happened, how many attempts occurred, and how a workflow recovered.

