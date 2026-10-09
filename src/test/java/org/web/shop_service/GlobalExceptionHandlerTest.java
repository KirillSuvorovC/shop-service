package org.web.shop_service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.web.shop_service.auth.JwtRequest;
import org.web.shop_service.support.TestExceptionController;
import org.web.shop_service.support.configs.TestSecurityAOPConfig;
import org.web.shop_service.utils.JsonUtils;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Проверяет обработку исключений и формат ошибки
 */
@WebMvcTest(TestExceptionController.class)
@ActiveProfiles("test")
@Import(TestSecurityAOPConfig.class)
public class GlobalExceptionHandlerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Должен вернуть 400 и корректный формат ошибки")
    void shouldReturnValidResponseError() throws Exception {
        final var actions = mockMvc.perform(post(
                "/test/exceptions/400/method-argument-not-valid-exception")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.asJson(new JwtRequest(null, null)))
                        .with(csrf()));
        performRequest(actions, HttpStatus.BAD_REQUEST);
    }

    @ParameterizedTest
    @MethodSource("urls")
    @DisplayName("Должен вернуть ожидаемый статус и корректный формат ошибки")
    void shouldReturnValidResponseError(HttpStatus expected,
                                        String urlTemplate)
                                        throws Exception {
        final var actions = mockMvc.perform(post("/test/exceptions" + urlTemplate)
                                            .with(csrf()));
        performRequest(actions, expected);
    }

    private void performRequest(ResultActions resultActions, HttpStatus expected)
                                throws Exception {
        resultActions
                .andExpect(status().is(expected.value()))
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").value(expected.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    static Stream<Arguments> urls() {
        return Stream.of(
                arguments(HttpStatus.BAD_REQUEST, "/400/validation-exception"),
                arguments(HttpStatus.BAD_REQUEST, "/400/invalid-request-exception"),

                arguments(HttpStatus.NOT_FOUND, "/404/username-not-found-exception"),
                arguments(HttpStatus.NOT_FOUND, "/404/entity-not-found-exception"),

                arguments(HttpStatus.CONFLICT, "/409/user-already-exists-exception"),
                arguments(HttpStatus.CONFLICT, "/409/optimistic-locking-failure-exception"),
                arguments(HttpStatus.CONFLICT, "/409/optimistic-lock-exception"),

                arguments(HttpStatus.PRECONDITION_FAILED,
                        "/412/http-header-if-match-required-exception")
        );
    }
}
