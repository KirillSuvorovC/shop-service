package org.web.shop_service.idempotence;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.web.shop_service.configs.CaffeineCacheConfig;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = {
        IdempotenceKeyService.class,
        CaffeineCacheConfig.class,
        CaffeineCacheManager.class
})
public class IdempotencyKeyServiceTest {
    @Autowired
    private IdempotenceKeyService service;
    @Autowired
    private CacheManager cacheManager; // чтобы очищать кэш

    @BeforeEach
    void setUp() {
        cacheManager.getCache("idempotency_keys").clear();
    }

    @RepeatedTest(10)
    @DisplayName("""
            Выполняется только одна задача при
            одном и том же UUID-ключе
            """)
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @Execution(ExecutionMode.SAME_THREAD)
    void noShouldExecuteDuplicateTasks() throws Exception {
        // Given
        final int threads = 8;
        final ExecutorService executor = Executors.newFixedThreadPool(threads);
        final CountDownLatch starter = new CountDownLatch(1);
        final CountDownLatch done = new CountDownLatch(threads);
        final AtomicInteger counter = new AtomicInteger(0);

        final String uuidKey = "b18794e8-5d0d-417c-b361-ba38e78411b4";
        final String operationId = "counter:increment";
        final Runnable task = () -> {
            try {
                starter.await();
                service.process(uuidKey, operationId, counter::incrementAndGet);
            } catch (Exception e) {}
            finally {
                done.countDown();
            }
        };

        // Сначала выполняем операцию одновременно в разных потоках
        for (int i = 0; i < threads; i++) {
            executor.submit(task);
        }

        // Actions
        starter.countDown();
        done.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        // Затем пытаемся получить закэшированный результат задачи
        final int currentNumber = service.process(uuidKey,
                    operationId, counter::incrementAndGet);

        // Check
        assertThat(currentNumber).isEqualTo(1);
        assertThat(counter.get()).isEqualTo(1);
    }
}
