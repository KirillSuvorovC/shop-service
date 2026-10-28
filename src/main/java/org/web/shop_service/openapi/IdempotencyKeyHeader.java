package org.web.shop_service.openapi;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Используется для ввода HTTP-заголовка Idempotency-Key
 * в формате UUID.
 * Отображается как параметр запроса в Swagger UI
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Parameter(
        in = ParameterIn.HEADER,
        name = CustomHTTPHeaders.IDEMPOTENCY_KEY,
        description = """
                Ключ идемпотентности в формате UUID - для защиты от
                дублирующих запросов.
                """,
        required = true,
        example = "3e503391-cbf8-4082-8a3a-a04b720af0e1")
public @interface IdempotencyKeyHeader {
}
