package org.web.shop_service.idempotence;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Service
public class IdempotenceKeyService {
    private final Map<UUID, CompletableFuture<Object>> inProcess = new ConcurrentHashMap<>();

    @Cacheable(cacheNames = "idempotency_keys", key = "{#key,#returnType}")
    public <T> T process(String key, Supplier<T> operation, Class<T> returnType) {
        final UUID uuidKey = UUID.fromString(key);

        // Атомарно добавить задачу
        final var task = new CompletableFuture<Object>()
                                .orTimeout(5, TimeUnit.SECONDS);
        final var existingTask = inProcess.putIfAbsent(uuidKey, task);

        // Ждем результат, если другой поток уже выполняет запрос
        if (existingTask != null) return (T) existingTask.join();

        // Выполняем операцию
        try {
            final var result = operation.get();
            task.complete(result);
            return result;
        } catch (Exception e) {
            task.completeExceptionally(e);
            throw e;
        } finally {
            inProcess.remove(uuidKey);
        }
    }
}
