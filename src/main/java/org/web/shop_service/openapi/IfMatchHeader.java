package org.web.shop_service.openapi;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Используется для ввода HTTP-заголовка If-Match
 * в виде параметра в Swagger UI
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Parameter(
        in = ParameterIn.HEADER,
        name = "If-Match",
        description = """
                Текущая версия ресурса (ETag).
                Обязателен для оптимистичной блокировки.
                """,
        required = true,
        example = "0"
)
public @interface IfMatchHeader {
}
