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
* Оптимистичная блокировка при редактировании заказа, продукта
* Гарантированная публикация событий через Transaction Outbox
* История изменения цен продуктов

## Ключевые решения
* **Transaction Outbox** — гарантия доставки событий
* **Идемпотентный потребитель** — защита от дублей при повторной доставке
* **Оптимистичная блокировка** (`If-Match`) — безопасный конкурентный доступ
* **JWT (15 мин TTL)** — stateless-авторизация без похода в БД

## Требования
- Docker + Docker Compose

## Запуск
1. Клонируйте репозиторий:
```bash
git clone https://github.com/KirillSuvorovC/shop-service.git
cd shop-service
```
2. Скопируйте `.env.example` в `.env` и заполните значения.
3. Запустите:
```bash
docker compose up
```
4. API: http://localhost:8080
   Swagger: http://localhost:8080/swagger-ui.html
5. Для остановки:
```bash
docker compose down
```

## Тесты
162 теста (unit · slice · integration · E2E), ~3-6 мин.
Запустить тесты:
```bash
./mvnw test
```
