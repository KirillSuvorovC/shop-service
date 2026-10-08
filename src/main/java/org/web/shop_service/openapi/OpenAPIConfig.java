package org.web.shop_service.openapi;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import org.web.shop_service.utils.ResponseError;

@Configuration
public class OpenAPIConfig {
    public static final String BEARER_AUTH = "bearerAuth";

    /**
     * Регистрирует схему явно, так как SpringDoc
     * не делает этого автоматически.
     * @return лямбда для регистрации схемы {@link ResponseError}
     */
    @Bean
    public OpenApiCustomizer responseErrorSchema() {
        return openApi -> {
            var resolvedSchema = ModelConverters.getInstance()
                    .resolveAsResolvedSchema(new AnnotatedType(ResponseError.class));
            openApi.getComponents().addSchemas("ResponseError", resolvedSchema.schema);
        };
    }

    /**
     * Удаляет схему, так как {@link Pageable} используется
     * только в виде параметров, а не в виде JSON.
     * @return лямбда для удаления схемы {@link Pageable}
     * @see RuPageableAsQueryParam
     */
    @Bean
    public OpenApiCustomizer removedPageableSchema() {
        return openApi -> openApi.getComponents()
                    .getSchemas().remove("Pageable");
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .in(SecurityScheme.In.COOKIE)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("""
                JWT хранится в Cookie и устанавливается автоматически
                после POST /api/auth/login. Кнопка Authorize и замки
                не требуются для работы.
                Для тестирования: выполните login через Swagger UI,
                Cookie установится автоматически.
                """)));
    }
}
