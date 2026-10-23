# Order Service CQRS Pattern with File-Based H2 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement the CQRS (Command Query Responsibility Segregation) pattern in `order` service with a file-based H2 database, separating write operations (`Order`) and denormalized read projections (`OrderView`), synchronized asynchronously via RabbitMQ.

**Architecture:** The write path (`OrderCommandService`) validates inputs, calls Eureka clients, persists `Order` entities to H2 table `orders`, and publishes events to RabbitMQ exchange `orders.exchange`. An internal consumer (`OrderQuerySyncConsumer`) consumes events from `orders.query.sync` queue to maintain the denormalized read model table `order_views`. The read path (`OrderQueryService`) reads directly from `order_views`.

**Tech Stack:** Java 21, Spring Boot 4.1.1, Spring Data JPA, H2 Database (file-based), Spring AMQP (RabbitMQ), JUnit 5, Mockito.

**Spec:** `docs/superpowers/specs/2026-10-23-order-cqrs-h2-design.md`

## Global Constraints

- Preserve Java 21, Spring Boot 4.1.1, Spring Cloud 2025.1.3 versions.
- Do not add unnecessary third-party libraries (no Axon framework; use standard Spring AMQP and Spring Data JPA).
- H2 file database path must be `./data/orderdb`.
- RabbitMQ exchange `orders.exchange` must remain `DirectExchange` to maintain compatibility with `email-service`.
- REST endpoints in `OrderController` must remain backward compatible (`/api/orders`).

## Review Focus

1. Eventual consistency lag: Read model (`OrderView`) is updated asynchronously via RabbitMQ; tests must verify consumer properly updates read model upon receiving created/updated/deleted events.
2. DirectExchange routing keys: `orders.exchange` must route `orders.created`, `orders.updated`, and `orders.deleted` to `orders.query.sync`, while `email-service` continues receiving `orders.created` on `orders.confirmation`.
3. Update and Delete events: Changes to orders must update or remove corresponding `OrderView` records in `order_views`.
4. Backward compatibility: `OrderView` must expose both `id` and `orderId` so JSON serialization remains compatible with existing Postman collection assertions.
5. In-test Eureka and RabbitMQ isolation: Tests must mock external services/brokers or use lightweight test configurations without requiring live RabbitMQ or Eureka at build time.

---

### Task 1: Configure File-Based H2 Database

**Files:**
- Modify: `order/src/main/resources/application.properties`
- Modify: `.gitignore`

**Interfaces:**
- Consumes: none
- Produces: H2 file database at `order/data/orderdb`

- [ ] **Step 1: Update `.gitignore` to ignore H2 database files**
Add `**/data/` and `*.mv.db` and `*.trace.db` to `.gitignore`.

- [ ] **Step 2: Update `order/src/main/resources/application.properties`**
Change `spring.datasource.url` from `jdbc:h2:mem:orderdb;DB_CLOSE_DELAY=-1` to `jdbc:h2:file:./data/orderdb;DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE`.

- [ ] **Step 3: Verify context loads with file-based H2**
Run: `order/mvnw.cmd test -Dtest=OrderApplicationTests` in `order/`
Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**
```bash
git add .gitignore order/src/main/resources/application.properties
git commit -m "config(order): switch H2 datasource to file-based storage"
```

---

### Task 2: Implement Read Model (`OrderView` Entity and `OrderViewRepository`)

**Files:**
- Create: `order/src/main/java/com/faizul/order/entity/OrderView.java`
- Create: `order/src/main/java/com/faizul/order/repository/OrderViewRepository.java`
- Create: `order/src/test/java/com/faizul/order/repository/OrderViewRepositoryTests.java`

**Interfaces:**
- Consumes: none
- Produces: `OrderView` JPA entity mapped to `order_views`, `OrderViewRepository` interface extending `JpaRepository<OrderView, Long>`

- [ ] **Step 1: Write repository test for `OrderViewRepository`**
Create `OrderViewRepositoryTests` verifying `save`, `findById`, and `findAll` operations on `OrderView`.

- [ ] **Step 2: Run test to verify it fails**
Run: `order/mvnw.cmd test -Dtest=OrderViewRepositoryTests` in `order/`
Expected: FAIL (class `OrderView` / `OrderViewRepository` not found)

- [ ] **Step 3: Implement `OrderView` and `OrderViewRepository`**
Define `OrderView` with fields:
- `@Id private Long orderId;`
- `private Long productId;`
- `private String namaProduk;`
- `private Long pelangganId;`
- `private String namaPelanggan;`
- `private String emailPelanggan;`
- `private LocalDate tanggalTransaksi;`
- `private Integer jumlah;`
- `private BigDecimal total;`
- Include `public Long getId() { return orderId; }` for JSON compatibility.
Define `OrderViewRepository extends JpaRepository<OrderView, Long>`.

- [ ] **Step 4: Run repository tests**
Run: `order/mvnw.cmd test -Dtest=OrderViewRepositoryTests` in `order/`
Expected: BUILD SUCCESS

- [ ] **Step 5: Commit**
```bash
git add order/src/main/java/com/faizul/order/entity/OrderView.java order/src/main/java/com/faizul/order/repository/OrderViewRepository.java order/src/test/java/com/faizul/order/repository/OrderViewRepositoryTests.java
git commit -m "feat(order): add OrderView read model and repository"
```

---

### Task 3: Event Models and RabbitMQ Topology for Query Synchronization

**Files:**
- Create: `order/src/main/java/com/faizul/order/event/OrderUpdatedEvent.java`
- Create: `order/src/main/java/com/faizul/order/event/OrderDeletedEvent.java`
- Modify: `order/src/main/java/com/faizul/order/event/OrderEventPublisher.java`
- Modify: `order/src/main/java/com/faizul/order/event/RabbitOrderEventPublisher.java`
- Modify: `order/src/main/java/com/faizul/order/config/RabbitConfig.java`
- Test: `order/src/test/java/com/faizul/order/event/RabbitOrderEventPublisherTests.java`

**Interfaces:**
- Consumes: RabbitMQ exchange `orders.exchange`
- Produces:
  - `OrderUpdatedEvent(Long orderId, LocalDate tanggalTransaksi, String namaPelanggan, String emailPelanggan, String namaProduk, Integer jumlah, BigDecimal total)`
  - `OrderDeletedEvent(Long orderId)`
  - `OrderEventPublisher.publish(OrderUpdatedEvent event)`
  - `OrderEventPublisher.publish(OrderDeletedEvent event)`
  - Queue `orders.query.sync` bound to `orders.exchange` with `orders.created`, `orders.updated`, `orders.deleted`

- [ ] **Step 1: Write publisher unit tests**
Create `RabbitOrderEventPublisherTests` testing publishing created, updated, and deleted events with respective routing keys (`orders.created`, `orders.updated`, `orders.deleted`).

- [ ] **Step 2: Run test to verify it fails**
Run: `order/mvnw.cmd test -Dtest=RabbitOrderEventPublisherTests` in `order/`
Expected: Compilation failure (events and publisher methods do not exist)

- [ ] **Step 3: Implement events and update publisher and RabbitConfig**
1. Create `OrderUpdatedEvent` (record or class with getters and no-args constructor).
2. Create `OrderDeletedEvent` (record or class with `orderId`).
3. Add `void publish(OrderUpdatedEvent event)` and `void publish(OrderDeletedEvent event)` to `OrderEventPublisher`.
4. Implement methods in `RabbitOrderEventPublisher`:
   - `publish(OrderCreatedEvent event)` -> send to `EXCHANGE`, `"orders.created"`, event
   - `publish(OrderUpdatedEvent event)` -> send to `EXCHANGE`, `"orders.updated"`, event
   - `publish(OrderDeletedEvent event)` -> send to `EXCHANGE`, `"orders.deleted"`, event
5. In `RabbitConfig.java`:
   - Declare `Queue querySyncQueue()` with name `"orders.query.sync"`.
   - Add bindings between `querySyncQueue` and `orderExchange` for keys `"orders.created"`, `"orders.updated"`, `"orders.deleted"`.

- [ ] **Step 4: Run publisher tests**
Run: `order/mvnw.cmd test -Dtest=RabbitOrderEventPublisherTests` in `order/`
Expected: BUILD SUCCESS

- [ ] **Step 5: Commit**
```bash
git add order/src/main/java/com/faizul/order/event/ order/src/main/java/com/faizul/order/config/RabbitConfig.java order/src/test/java/com/faizul/order/event/
git commit -m "feat(order): add updated and deleted events with query sync queue bindings"
```

---

### Task 4: Command Path (`OrderCommandService`)

**Files:**
- Create: `order/src/main/java/com/faizul/order/service/OrderCommandService.java`
- Modify: `order/src/main/java/com/faizul/order/service/OrderService.java` (delegate or replace write operations)
- Create: `order/src/test/java/com/faizul/order/service/OrderCommandServiceTests.java`

**Interfaces:**
- Consumes: `OrderRepository`, `ProductClient`, `CustomerClient`, `OrderEventPublisher`
- Produces:
  - `OrderCommandService.createOrder(Order order): Order`
  - `OrderCommandService.updateOrder(Long id, Order order): Order`
  - `OrderCommandService.deleteOrder(Long id): boolean`

- [ ] **Step 1: Write unit tests in `OrderCommandServiceTests`**
Test `createOrder`, `updateOrder`, and `deleteOrder`:
- Verify input validation (null product/customer, invalid quantity).
- Verify total calculation using fetched product price.
- Verify persistence to `OrderRepository`.
- Verify publishing `OrderCreatedEvent`, `OrderUpdatedEvent`, and `OrderDeletedEvent` via `TransactionSynchronizationManager.afterCommit()`.

- [ ] **Step 2: Run test to verify it fails**
Run: `order/mvnw.cmd test -Dtest=OrderCommandServiceTests` in `order/`
Expected: FAIL (class `OrderCommandService` not found)

- [ ] **Step 3: Implement `OrderCommandService`**
Move write logic from `OrderService` into `@Service public class OrderCommandService`:
- `createOrder`: lookup product and customer, calculate total, save to repo, register afterCommit synchronization to publish `OrderCreatedEvent`.
- `updateOrder`: find existing order by id, lookup updated product/customer if changed, recalculate total, save, register afterCommit synchronization to publish `OrderUpdatedEvent`.
- `deleteOrder`: check existsById, delete, register afterCommit synchronization to publish `OrderDeletedEvent`.

- [ ] **Step 4: Run `OrderCommandServiceTests`**
Run: `order/mvnw.cmd test -Dtest=OrderCommandServiceTests` in `order/`
Expected: BUILD SUCCESS

- [ ] **Step 5: Commit**
```bash
git add order/src/main/java/com/faizul/order/service/OrderCommandService.java order/src/test/java/com/faizul/order/service/OrderCommandServiceTests.java
git commit -m "feat(order): implement OrderCommandService with post-commit event publishing"
```

---

### Task 5: Query Path and Sync Consumer (`OrderQuerySyncConsumer` & `OrderQueryService`)

**Files:**
- Create: `order/src/main/java/com/faizul/order/messaging/OrderQuerySyncConsumer.java`
- Create: `order/src/main/java/com/faizul/order/service/OrderQueryService.java`
- Create: `order/src/test/java/com/faizul/order/messaging/OrderQuerySyncConsumerTests.java`
- Create: `order/src/test/java/com/faizul/order/service/OrderQueryServiceTests.java`

**Interfaces:**
- Consumes: `orders.query.sync` queue, `OrderViewRepository`
- Produces:
  - `OrderQuerySyncConsumer.handleOrderCreated(OrderCreatedEvent event)`
  - `OrderQuerySyncConsumer.handleOrderUpdated(OrderUpdatedEvent event)`
  - `OrderQuerySyncConsumer.handleOrderDeleted(OrderDeletedEvent event)`
  - `OrderQueryService.getAllOrders(): List<OrderView>`
  - `OrderQueryService.getOrderById(Long id): OrderView`

- [ ] **Step 1: Write consumer and query service tests**
1. In `OrderQuerySyncConsumerTests`:
   - Test `handleOrderCreated` inserts new `OrderView` into `OrderViewRepository`.
   - Test `handleOrderUpdated` updates existing `OrderView`.
   - Test `handleOrderDeleted` deletes `OrderView` by `orderId`.
2. In `OrderQueryServiceTests`:
   - Test `getAllOrders` calls `orderViewRepository.findAll()`.
   - Test `getOrderById` calls `orderViewRepository.findById(id)`.

- [ ] **Step 2: Run tests to verify they fail**
Run: `order/mvnw.cmd test -Dtest=OrderQuerySyncConsumerTests,OrderQueryServiceTests` in `order/`
Expected: FAIL

- [ ] **Step 3: Implement `OrderQuerySyncConsumer` and `OrderQueryService`**
1. Implement `OrderQuerySyncConsumer` annotated with `@Component`:
   - Use `@RabbitListener(queues = "orders.query.sync")` with `@RabbitHandler` methods for `OrderCreatedEvent`, `OrderUpdatedEvent`, and `OrderDeletedEvent`.
   - Map event fields to `OrderView` entity and save/delete in `OrderViewRepository`.
2. Implement `OrderQueryService` annotated with `@Service`:
   - Inject `OrderViewRepository` and return `List<OrderView>` or `Optional<OrderView>`.

- [ ] **Step 4: Run consumer and query service tests**
Run: `order/mvnw.cmd test -Dtest=OrderQuerySyncConsumerTests,OrderQueryServiceTests` in `order/`
Expected: BUILD SUCCESS

- [ ] **Step 5: Commit**
```bash
git add order/src/main/java/com/faizul/order/messaging/ order/src/main/java/com/faizul/order/service/OrderQueryService.java order/src/test/java/com/faizul/order/
git commit -m "feat(order): implement OrderQuerySyncConsumer and OrderQueryService"
```

---

### Task 6: Refactor Controller and Verify End-to-End CQRS Flow

**Files:**
- Modify: `order/src/main/java/com/faizul/order/controller/OrderController.java`
- Modify: `order/src/test/java/com/faizul/order/service/OrderServiceTests.java` (or replace with updated controller/integration tests)
- Create: `order/src/test/java/com/faizul/order/controller/OrderControllerTests.java`

**Interfaces:**
- Consumes: `OrderCommandService`, `OrderQueryService`
- Produces: Backward-compatible REST endpoints `/api/orders`

- [ ] **Step 1: Write controller tests in `OrderControllerTests`**
Test that `GET /api/orders` calls `OrderQueryService.getAllOrders()`, `GET /api/orders/{id}` calls `OrderQueryService.getOrderById(id)`, and write operations delegate to `OrderCommandService`.

- [ ] **Step 2: Update `OrderController`**
Inject `OrderCommandService` and `OrderQueryService` into `OrderController`.
- `GET /api/orders` -> `orderQueryService.getAllOrders()`
- `GET /api/orders/{id}` -> `orderQueryService.getOrderById(id)`
- `POST /api/orders` -> `orderCommandService.createOrder(order)`
- `PUT /api/orders/{id}` -> `orderCommandService.updateOrder(id, order)`
- `DELETE /api/orders/{id}` -> `orderCommandService.deleteOrder(id)`

- [ ] **Step 3: Run full suite of tests in `order`**
Run: `order/mvnw.cmd clean test` in `order/`
Expected: All tests pass (0 failures, 0 errors).

- [ ] **Step 4: Commit**
```bash
git add order/src/main/java/com/faizul/order/controller/OrderController.java order/src/test/java/com/faizul/order/
git commit -m "feat(order): wire OrderController to CQRS command and query services"
```
