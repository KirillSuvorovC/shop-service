# Сервис магазина

REST API интернет-магазина с JWT-аутентификацией,
оптимистичной блокировкой и надёжной публикацией событий
через Transaction Outbox.

## Стек технологий
* Java 17, Spring Boot 3.5
* PostgreSQL, Flyway
* RabbitMQ (Transactional Outbox)
* Docker, docker-compose
* Testcontainers, JUnit 5, Mockito

## Возможности
* Регистрация и аутентификация через JWT
* Ролевая модель доступа (USER, ADMIN)
* CRUD для заказов, продуктов, пользователей
* Валидация входных данных с единым форматом ошибок (`@RestControllerAdvice`)
* Оптимистичная блокировка при редактировании заказа
* Гарантированная публикация событий через Transaction Outbox
* История изменения цен продуктов

## Ключевые решения
* **Transaction Outbox** — гарантия доставки событий
* **Идемпотентный потребитель** — защита от дублей при повторной доставке
* **Оптимистичная блокировка** (`If-Match`) — безопасный конкурентный доступ
* **JWT (15 мин TTL)** — stateless-авторизация без похода в БД

## Запуск
```bash
docker-compose up
```
Swagger: http://localhost:8080/swagger-ui.html
d
## Тесты
162 теста (unit · integration · slice · E2E), ~3-6 мин
```bash
./mvnw test
```