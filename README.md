# Hotel Management System — Microservices

Spring Boot 3 / Spring Cloud 2023 microservices backend, split by business domain, with
Eureka service discovery, a Spring Cloud Gateway entry point, MySQL-per-service, and
Swagger/OpenAPI on every service.

## Modules

| Module | Port | Owner (suggested) | Responsibility |
|---|---|---|---|
| `eureka-server` | 8761 | shared/infra | Service registry |
| `api-gateway` | 8080 | shared/infra | Single entry point, routing, JWT check |
| `common-security` | – | shared/infra | Shared JJWT helper (library, no HTTP port) |
| `auth-service` | 8081 | Member 1 | Users, **roles**, JWT login/register/refresh |
| `room-service` | 8082 | Member 2 | Room classes, rooms, hotel service menu |
| `booking-service` | 8083 | Member 3 | Reservations, availability, check-in/out, occupants |
| `billing-service` | 8084 | Member 4 | Folios, folio items, payments, revenue reports |
| `notification-service` | 8085 | Member 5 | Guest notifications + message templates |
| `review-service` | 8086 | Member 5 | Post-stay guest reviews + staff replies |

Each business service owns its own MySQL schema (`auth_db`, `room_db`, `booking_db`,
`billing_db`, `notification_db`, `review_db`) — no service reaches into another service's
database. Cross-domain lookups (e.g. booking-service checking that a `customer_id` exists
in auth-service) go over load-balanced `WebClient` calls resolved through Eureka.

`notification-service` and `review-service` used to be one bundled service; they're now
split so a 5-person team can divide work either as 5 clean service-owners (with member 5
covering both small services) or by pairing one of them with another member's service.

## Auth model (roles table)

`roles(id, role_name, description)` is a real table, seeded on auth-service startup with
`ADMIN`, `RECEPTIONIST`, `HOUSEKEEPING`, `CUSTOMER` (see `auth-service/src/main/resources/data.sql`).
`users.role_id` is a foreign key to it (one-to-many: one role, many users).

## Running it

1. **Start MySQL** (creates all 6 databases and a `hotel_user`/`hotel_pass` account via
   `docker/mysql-init.sql`):
   ```
   docker compose up -d
   ```
   > If you already ran `docker compose up` before `notification_db`/`review_db` existed,
   > the init script won't re-run against the existing volume. Either
   > `docker compose down -v && docker compose up -d` (destroys all local dev data) or run
   > `docker/mysql-init.sql` manually against the running container.

2. **Build everything once** from the repo root (also installs `common-security` into
   the local Maven repo for the other modules):
   ```
   mvn clean install
   ```

3. **Start services**, each in its own terminal, in this order:
   ```
   mvn -pl eureka-server spring-boot:run
   mvn -pl auth-service spring-boot:run
   mvn -pl room-service spring-boot:run
   mvn -pl booking-service spring-boot:run
   mvn -pl billing-service spring-boot:run
   mvn -pl notification-service spring-boot:run
   mvn -pl review-service spring-boot:run
   mvn -pl api-gateway spring-boot:run
   ```
   Check registration at the Eureka dashboard: http://localhost:8761

## Swagger / OpenAPI

- **Aggregated (recommended)**: http://localhost:8080/swagger-ui.html — dropdown in the
  top-right switches between all 6 services, requests go through the gateway.
- **Per-service direct**:
  - http://localhost:8081/api/auth/swagger-ui.html
  - http://localhost:8082/api/catalog/swagger-ui.html
  - http://localhost:8083/api/bookings/swagger-ui.html
  - http://localhost:8084/api/billing/swagger-ui.html
  - http://localhost:8085/api/notifications/swagger-ui.html
  - http://localhost:8086/api/reviews/swagger-ui.html

## Auth flow

1. `POST /api/auth/register` — public, defaults to role `CUSTOMER` if `roleName` is
   omitted. Returns a JWT.
2. `POST /api/auth/login` — public, returns a JWT.
3. Every other route behind the gateway requires `Authorization: Bearer <token>`.
   The gateway validates the token's signature/expiry and forwards `X-User-Id`,
   `X-Username`, `X-User-Roles` headers to the downstream service.
4. `POST /api/auth/refresh` — exchange a still-valid token for a new one with a fresh
   expiry (sliding session; no separate refresh-token table).
5. `GET /api/auth/me` / `PUT /api/auth/me/password` — profile and self-service password
   change for the caller identified by the gateway's `X-User-Id` header.
6. To create a staff account (ADMIN/RECEPTIONIST/HOUSEKEEPING), register normally then
   have an admin call `PUT /api/auth/users/{id}` with `{"roleName": "RECEPTIONIST"}`.

## API surface by service

All list endpoints are paginated (`?page=&size=&sort=`) via Spring Data `Pageable`.

**auth-service** — tables: `users`, `roles`
- `POST /api/auth/register`, `POST /api/auth/login`, `POST /api/auth/refresh`
- `GET /api/auth/me`, `PUT /api/auth/me/password`
- `GET /api/auth/users?role=` (paginated), `GET/PUT/DELETE /api/auth/users/{id}`, `PUT /api/auth/users/{id}/password`
- `GET/POST/PUT/DELETE /api/auth/roles(/{id})`

**room-service** — tables: `room_classes`, `rooms`, `hotel_services`
- `GET /api/catalog/rooms?status=&roomClassId=` (paginated), `POST/PUT/DELETE /api/catalog/rooms(/{id})`
- `PATCH /api/catalog/rooms/{id}/status`
- `GET/POST/PUT/DELETE /api/catalog/room-classes(/{id})`
- `GET/POST/PUT/DELETE /api/catalog/services(/{id})`

**booking-service** — tables: `reservations`, `reservation_rooms`, `room_occupants`
- `GET /api/bookings/availability?checkInDate=&checkOutDate=&roomClassId=` — rooms with no
  overlapping booking in that date range
- `GET /api/bookings/reservations?customerId=&status=` (paginated), `POST /api/bookings/reservations`
- `GET /api/bookings/reservations/stats` — counts grouped by booking status
- `PATCH /api/bookings/reservations/{id}/check-in|check-out|cancel` — the normal lifecycle
  transitions (check-out/cancel auto-release the assigned rooms)
- `PATCH /api/bookings/reservations/{id}/status` — arbitrary status correction, for admin use
- `POST /api/bookings/reservation-rooms` (requires `checkInDate`/`checkOutDate`, validated
  against the same overlap check as `/availability`), `GET/DELETE /api/bookings/reservation-rooms(/{id})`
- `GET/POST/DELETE /api/bookings/room-occupants`

**billing-service** — tables: `folios`, `folio_items`, `payment_transactions`
- `GET /api/billing/folios` (paginated), `GET /api/billing/folios/unpaid` (balance > 0), `POST/DELETE /api/billing/folios(/{id})`
- `GET /api/billing/folios/{id}/statement` — balance + all items + all payments in one call
- `GET/POST/DELETE /api/billing/folio-items?folioId=`
- `GET/POST/DELETE /api/billing/payments?folioId=`
- `GET /api/billing/reports/revenue?from=&to=` — net revenue (deposits + final payments − refunds)

**notification-service** — tables: `notifications`, `notification_templates`
- `GET/POST/DELETE /api/notifications?userId=` (paginated) — simulated send, no real email/SMS provider;
  `message` is optional — if omitted, the registered template for that `type` is used
- `GET/POST/PUT/DELETE /api/notifications/templates(/{id})` — one template per `NotificationType`,
  seeded on startup (see `notification-service/src/main/resources/data.sql`)

**review-service** — tables: `reviews`, `review_replies`
- `GET/POST/PUT/DELETE /api/reviews?roomId=|reservationId=` (paginated)
- `GET /api/reviews/room/{roomId}/average` — average rating + review count
- `GET/POST /api/reviews/{reviewId}/replies`, `DELETE /api/reviews/{reviewId}/replies/{id}` — staff
  responses to a review (`review_replies.review_id` is a real DB foreign key, since both tables
  live in `review_db`)

## Example end-to-end flow

```
POST /api/auth/register                 -> create a CUSTOMER, get JWT
POST /api/catalog/room-classes          -> create "Deluxe" room class (as staff)
POST /api/catalog/rooms                 -> create room "101" in that class
GET  /api/bookings/availability         -> confirm room 101 is free for the wanted dates
POST /api/bookings/reservations         -> reservation for that customer (validates via auth-service)
POST /api/bookings/reservation-rooms    -> assign room 101 with check-in/check-out dates
POST /api/bookings/room-occupants       -> declare the guest staying in the room
PATCH /api/bookings/reservations/{id}/check-in  -> PENDING -> IN_HOUSE
POST /api/billing/folios                -> open a folio for the reservation (validates via booking-service)
POST /api/billing/folio-items           -> post a ROOM_CHARGE, balance increases
POST /api/billing/payments              -> record a CARD/CASH deposit, balance decreases
PATCH /api/bookings/reservations/{id}/check-out -> IN_HOUSE -> CHECKED_OUT, room -> DIRTY
GET  /api/billing/folios/{id}/statement -> final itemized bill
POST /api/notifications                 -> send a checkout/invoice notification (uses the INVOICE template)
POST /api/reviews                       -> guest leaves a rating for the room
POST /api/reviews/{reviewId}/replies    -> staff replies to the guest's review
```

## Notes / next steps

- JWT validation is centralized at `api-gateway`; downstream services trust the
  `X-User-*` headers rather than re-parsing the token. Fine for this project's scope;
  a production system would want mTLS or a signed-header scheme between gateway and
  services.
- All `ddl-auto: update` — fine for coursework, switch to Flyway/Liquibase migrations
  before any real deployment.
- No endpoint currently enforces role-based access control (any authenticated caller can
  hit any route) — the JWT already carries the role list in its `roles` claim and the
  gateway forwards it as `X-User-Roles`, so adding `@PreAuthorize`/`@Secured` checks per
  service is a natural next step if that's needed.
