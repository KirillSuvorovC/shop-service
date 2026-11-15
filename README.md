# Сервис магазина

REST API интернет-магазина с JWT-аутентификацией,
оптимистичной блокировкой и надёжной публикацией событий
через Transaction Outbox.

## Стек технологий
* Java 17, Spring Boot 3.5
* PostgreSQL, Flyway, Caffeine (local cache)
* RabbitMQ (Transactional Outbox)
* Docker, docker-compose
* Testcontainers, JUnit 5, RestAssured, Mockito

## Возможности
* Регистрация и аутентификация через JWT
* Ролевая модель доступа (USER, ADMIN)
* CRUD для заказов, продуктов, пользователей
* Валидация входных данных с единым форматом ошибок (`@RestControllerAdvice`)
* Оптимистичная блокировка при редактировании заказа, продукта
* Гарантированная публикация событий через Transaction Outbox
* История изменения цен продуктов

## Ключевые решения
* **Transaction Outbox** - гарантия доставки событий
* **Идемпотентный потребитель** - защита от дублей при повторной доставке
* **Идемпотентные POST-запросы** - защита от дублей через Idempotency-Key
(Caffeine + CompletableFuture); операция выполняется не более одного раза
* **Переиспользование Testcontainers** - за счет этого время на прогон
всех тестов уменьшилось на 60-70%. Данные в тестовой БД очищаются между
тестами
* **Кэширование (Caffeine)** - для эндпоинтов 'GET /api/products/{id}',
'GET /api/orders/{id}'; списки с пагинацией не кэшируются из-за сложной инвалидации
* **Оптимистичная блокировка** (`If-Match`) - безопасный конкурентный доступ
* **JWT (15 мин TTL)** - stateless-авторизация без похода в БД

## Требования
- Docker + Docker Compose

## Запуск
1. Клонируйте репозиторий:
```bash
git clone https://github.com/KirillSuvorovC/shop-service.git
cd shop-service
```
2. Скопируйте шаблон `.env.example` в новый файл `.env` и заполните значения.
3. Запустите:
```bash
docker compose up
```
4. API: http://localhost:8080 \
   Swagger: http://localhost:8080/swagger-ui.html (недоступен для профиля prod)
5. Для остановки:
```bash
docker compose down
```

## Тесты
Внедрен CI, который запускает все 180 тестов: юнит-тесты, слайс-тесты, интеграционные и сквозные (E2E) тесты.
Интеграционные и E2E тесты используют Testcontainers для поднятия реальных зависимостей (PostgreSQL, RabbitMQ).
В среднем, прогон всех тестов длится 1-1.5 минуты. Критичные сценарии вынесены в smoke-тесты
(тег `smoke`). Статус сборки и логи доступны во вкладке [Actions](https://github.com/KirillSuvorovC/shop-service/actions).\
Как запустить тесты:
1. Запустить Docker
2. Запуск всех тестов:
   - Windows: `.\mvnw test`
   - Linux/macOS: `./mvnw test`
3. Запуск только smoke-тестов:
   - Windows: `.\mvnw test -Dgroups=smoke`
   - Linux/macOS: `./mvnw test -Dgroups=smoke`
