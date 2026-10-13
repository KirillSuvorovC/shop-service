package org.web.shop_service.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.web.shop_service.support.RestControllerTest;

import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RestControllerTest(controllers = DevAuthController.class)
@ActiveProfiles("prod")
class DevAuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Вернуть 404, если профиль - не 'dev'")
    void loginAsAdmin_shouldReturn404WhenActiveProfileIsNotDev() throws Exception {
        mockMvc.perform(post("/dev/api/auth/admin")
                .with(csrf()))
                .andExpect(status().isNotFound());
    }
}