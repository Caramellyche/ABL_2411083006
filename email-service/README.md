# Email Service

Independent Spring Boot service. Consumes `OrderCreatedEvent` from RabbitMQ queue `orders.confirmation`; no HTTP send-email endpoint.

## Local setup

1. Install and start RabbitMQ Server locally (no Docker). Default AMQP endpoint: `localhost:5672`; management UI is optional.
2. Run Eureka from `test/` on port `8761`.
3. Configure Gmail SMTP before starting this service:

```powershell
$env:MAIL_USERNAME="your-address@gmail.com"
$env:MAIL_PASSWORD="your-16-character-app-password"
```

Use Gmail App Password, not account password. Do not add values to source control or Postman exports. RabbitMQ can be overridden with `RABBITMQ_HOST`, `RABBITMQ_PORT`, `RABBITMQ_USERNAME`, and `RABBITMQ_PASSWORD`.

4. Start service: `./mvnw.cmd spring-boot:run` from `email-service/`.
5. Check health: `GET http://localhost:8084/api/emails/health`.
6. Run Product and Customer, create each record with their local Postman collections, then use returned IDs in `order/postman/order.postman_collection.json`. Email processing is asynchronous.
7. Check service logs for `Confirmation email sent for order <id>`. Failed messages retry twice, then route to `orders.confirmation.dlq`.

Each service uses H2 in-memory storage in current configuration. Restarting a service clears its local data.
