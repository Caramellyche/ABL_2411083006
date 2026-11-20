# ABL_2411083006

Spring microservices: Eureka (`test`), product, customer, order command/query,
auth, email, and API gateway.

## JWT configuration

Auth and gateway require the **same** `JWT_SECRET` environment variable.
There is no production default. Use a cryptographically random value of at
least 32 bytes, not the public test key in `src/test`.

Generate a new value in PowerShell without printing it:

```powershell
$bytes = New-Object byte[] 48
$rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
try { $rng.GetBytes($bytes) } finally { $rng.Dispose() }
$env:JWT_SECRET = [Convert]::ToBase64String($bytes)
```

Keep the value securely if services will be restarted in different terminals.
Changing the secret invalidates existing tokens: restart both auth and gateway
with the new value, then log in again.

### Local services

In the same PowerShell session:

```powershell
.\start-all.bat
```

RabbitMQ must be available locally. The launcher prompts for Gmail credentials
in the email-service window; do not commit those credentials.
If launching auth or gateway separately, set the same `JWT_SECRET` in each
terminal first. Spring does not automatically load the root `.env` file.

### Docker

Compose forwards `JWT_SECRET` from the environment to the gateway:

```powershell
docker compose up --build -d
```

Alternatively, copy `.env.example` to `.env` and fill in a securely generated
value. `.env` is ignored by Git. Never commit real secret values.

The current Compose file runs only Eureka, gateway, and product. It does not yet
deploy auth, customer, order, email, or RabbitMQ.

For a standalone gateway container, pass the existing environment variable:

```powershell
docker run -d --name api-gateway-container -p 8080:8080 -e JWT_SECRET api-gateway:1.0
```

Rebuild images after source changes. Configure Eureka networking separately when
running standalone containers.

## Tests

Run `.\mvnw.cmd test` inside each service folder. Test secrets are fixtures only,
and test databases use the test configuration.
