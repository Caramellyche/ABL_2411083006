# Order Service CQRS Pattern with File-Based H2 Design

## Goal

Implement CQRS (Command Query Responsibility Segregation) pattern in `order` service using file-based H2 database (`./data/orderdb`), separating the write model (`Order` entity / `orders` table) from the read model (`OrderView` entity / `order_views` denormalized table), and synchronizing the read model via RabbitMQ events (`orders.exchange`).

## Context and Constraints

- Project path: `order/`
- Java 21, Spring Boot 4.1.1, Spring Cloud 2025.1.3.
- Database: H2 file-based stored at `./data/orderdb`.
- RabbitMQ: `orders.exchange` (DirectExchange), existing queue `orders.confirmation` used by `email-service`.
- Add new durable queue `orders.query.sync` bound to `orders.exchange` for synchronizing read projections.
- Existing REST endpoints on `OrderController` (`POST /api/orders`, `PUT /api/orders/{id}`, `DELETE /api/orders/{id}`, `GET /api/orders`, `GET /api/orders/{id}`) must remain backward-compatible for Postman tests.

## Architecture

```
[HTTP Request]
   |
   +--> POST/PUT/DELETE --> OrderController
   |                           |
   |                           v
   |                     OrderCommandService
   |                           |
   |        +------------------+-------------------+
   |        |                                      |
   |        v                                      v
   |   [Write DB: orders]                 [RabbitMQ: orders.exchange]
   |                                               |
   |                                  +------------+------------+
   |                                  |                         |
   |                                  v                         v
   |                     [orders.query.sync]          [orders.confirmation]
   |                              |                             |
   |                              v                             v
   |                    OrderQuerySyncConsumer            email-service
   |                              |
   |                              v
   |                     [Read DB: order_views]
   |                              ^
   |                              |
   +--> GET /api/orders --------> OrderQueryService
```

## Detailed Components

### 1. Database Configuration
In `order/src/main/resources/application.properties`:
- Change `spring.datasource.url` to `jdbc:h2:file:./data/orderdb;DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE`.
- `spring.jpa.hibernate.ddl-auto=update`.

### 2. Domain Models & Repositories

#### Write Model (`Order` & `OrderRepository`)
- Entity `Order` mapped to table `orders`:
  - `id` (Long, PK generated)
  - `productId` (Long)
  - `pelangganId` (Long)
  - `tanggalTransaksi` (LocalDate)
  - `jumlah` (Integer)
  - `total` (BigDecimal)
- `OrderRepository` extends `JpaRepository<Order, Long>`.

#### Read Model (`OrderView` & `OrderViewRepository`)
- Entity `OrderView` mapped to table `order_views`:
  - `orderId` (Long, PK - mirrors `Order.id`)
  - `productId` (Long)
  - `namaProduk` (String)
  - `pelangganId` (Long)
  - `namaPelanggan` (String)
  - `emailPelanggan` (String)
  - `tanggalTransaksi` (LocalDate)
  - `jumlah` (Integer)
  - `total` (BigDecimal)
- `OrderViewRepository` extends `JpaRepository<OrderView, Long>`.

### 3. Events
- `OrderCreatedEvent(Long orderId, LocalDate tanggalTransaksi, String namaPelanggan, String emailPelanggan, String namaProduk, Integer jumlah, BigDecimal total)` (already exists, add getters/default constructor if needed).
- `OrderUpdatedEvent(Long orderId, LocalDate tanggalTransaksi, String namaPelanggan, String emailPelanggan, String namaProduk, Integer jumlah, BigDecimal total)`.
- `OrderDeletedEvent(Long orderId)`.

### 4. Messaging Configuration
In `RabbitConfig`:
- Add Queue `orders.query.sync`.
- Bind `orders.query.sync` to `orders.exchange` with routing keys:
  - `orders.created`
  - `orders.updated`
  - `orders.deleted`
- Update `OrderEventPublisher` interface & `RabbitOrderEventPublisher` to support publishing updated and deleted events.

### 5. Services

#### `OrderCommandService`
- `createOrder(Order order)`: validates inputs, fetches product & customer via Eureka RestClient, calculates total, saves to `OrderRepository`, publishes `OrderCreatedEvent` post-commit.
- `updateOrder(Long id, Order order)`: validates existing order, refreshes product/customer info and total, saves to `OrderRepository`, publishes `OrderUpdatedEvent` post-commit.
- `deleteOrder(Long id)`: deletes from `OrderRepository`, publishes `OrderDeletedEvent` post-commit.

#### `OrderQuerySyncConsumer`
- Listens to `orders.query.sync`.
- Handles `OrderCreatedEvent` and `OrderUpdatedEvent`: upserts `OrderView` record.
- Handles `OrderDeletedEvent`: deletes `OrderView` record by `orderId`.

#### `OrderQueryService`
- `getAllOrders()`: returns all `OrderView` records from `OrderViewRepository`.
- `getOrderById(Long id)`: returns `OrderView` from `OrderViewRepository`.

### 6. Controller (`OrderController`)
- Delegates write operations (`POST`, `PUT`, `DELETE`) to `OrderCommandService`.
- Delegates read operations (`GET /api/orders`, `GET /api/orders/{id}`) to `OrderQueryService`.
- Returns `OrderView` or compatible response for read queries.

## Testing Strategy
- Unit tests with Mockito for `OrderCommandService`, `OrderQueryService`, and `OrderQuerySyncConsumer`.
- Integration tests verifying `OrderController` delegating to command and query services.
- Verify Maven test compilation and execution.
