package org.web.shop_service.idempotence;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class IdempotenceKeyService {
    private final Map<String, CompletableFuture<Object>> inProcess = new ConcurrentHashMap<>();
    private final CacheManager manager;

    @PreDestroy
    private void dispose() {
        inProcess.clear();
    }

    /**
     * Выполняет операцию не более одного раза и кэширует результат.
     * <p>
     * Если потоки вызывают одновременно метод, один из них -
     * выполнит операцию, остальные - получат результат операции.
     * </p>
     * <p>
     * Основное применение этого метода - POST-запросы, которые
     * изначально неидемпотентны. Метод защищает от дублирующих
     * запросов.
     * </p>
     * <p>
     * Ключ кэша: {@code operationId + ':' + idempotenceKey}.
     * Один ключ идемпотентности может использоваться в разных
     * операциях и наоборот.
     * </p>
     * @param idempotenceKey ключ идемпотетности. Рекомендуется
     *                       использовать {@link UUID#randomUUID()}
     * @param operationId - вид операции
     * @param operation - операция для выполнения
     * @param <T> ожидаемый тип объекта
     * @return объект, полученный из {@code operation}
     */
    public <T> T process(String idempotenceKey, String operationId, Supplier<T> operation) {
        final String generatedCacheKey = generateKey(idempotenceKey, operationId);

        final var cacheKeys = manager.getCache("idempotency_keys");
        var cached = cacheKeys.get(generatedCacheKey);
        if(cached != null) return (T) cached.get();

        // Атомарно добавить задачу
        final var task = new CompletableFuture<Object>();
        final var existingTask = inProcess.putIfAbsent(generatedCacheKey, task);

        // Ждем результат, если другой поток уже выполняет запрос
        if (existingTask != null) {
            return (T) existingTask.join();
        }

        cached = cacheKeys.get(generatedCacheKey);
        if(cached != null) return (T) cached.get();

        // Выполняем операцию
        try {
            final var result = operation.get();
            cacheKeys.put(generatedCacheKey, result);
            task.complete(result);
            return result;
        } catch (Exception e) {
            task.completeExceptionally(e);
            throw e;
        } finally {
            inProcess.remove(generatedCacheKey);
        }
    }

    private String generateKey(String idempotenceKey, String operationId) {
        return operationId + ':' + idempotenceKey;
    }
}
