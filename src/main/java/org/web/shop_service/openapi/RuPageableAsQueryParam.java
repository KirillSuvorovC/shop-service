package org.web.shop_service.openapi;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Pageable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Используется для перевода схемы класса {@link Pageable}
 * на русский язык и ввода данных в виде параметров в Swagger UI
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Parameter(in = ParameterIn.QUERY, description = "Номер страницы (0..N)",
        name = "page", schema = @Schema(type = "integer", defaultValue = "0"))
@Parameter(in = ParameterIn.QUERY, description = "Размер страницы",
        name = "size", schema = @Schema(type = "integer", defaultValue = "20"))
@Parameter(in = ParameterIn.QUERY, description = "Сортировка: поле,asc|desc",
        name = "sort", array = @ArraySchema(schema = @Schema(type = "string", example = "asc")))
public @interface RuPageableAsQueryParam {}
