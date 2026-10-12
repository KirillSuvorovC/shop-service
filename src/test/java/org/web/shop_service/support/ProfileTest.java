package org.web.shop_service.support;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Используется для установки активного профиля {@code test}.
 * Свойства конфигураций из {@code application.properties},
 * требующие переменные окружения, указаны по умолчанию.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "jwt.secret=secretsecretsecretsecretsecretsecretsecret",
        "jwt.lifetime=15m"
})
public @interface ProfileTest {
}
