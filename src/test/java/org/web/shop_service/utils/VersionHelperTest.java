package org.web.shop_service.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class VersionHelperTest {

    @InjectMocks
    private VersionHelper versionHelper;

    @Test
    @DisplayName("Не бросает исключение, если версии совпадают")
    void shouldNotThrowWhenVersionsMatch() {
        // Given
        Long expectedVersion = 1000L;
        Long currentVersion = 1000L;

        // Action && Check
        assertThatCode(() -> versionHelper.checkVersion(expectedVersion, currentVersion))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Бросает OptimisticLockingFailureException, если версии не совпадают")
    void shouldThrowWhenVersionsMismatch() {
        // Given
        Long expectedVersion = 1000L;
        Long currentVersion = 1001L;

        // Action && Check
        assertThatThrownBy(() -> versionHelper.checkVersion(expectedVersion, currentVersion))
                .isInstanceOf(OptimisticLockingFailureException.class)
                .hasMessage("Data is old, reload page");
    }

    @Test
    @DisplayName("Бросает исключение, если expectedVersion = null, а currentVersion != null")
    void shouldThrowWhenExpectedIsNull() {
        // Given
        Long expectedVersion = null;
        Long currentVersion = 1000L;

        // Action && Check
        assertThatThrownBy(() -> versionHelper.checkVersion(expectedVersion, currentVersion))
                .isInstanceOf(OptimisticLockingFailureException.class);
    }

    @Test
    @DisplayName("Не бросает исключение, если обе версии null")
    void shouldNotThrowWhenBothNull() {
        // Given
        Long expectedVersion = null;
        Long currentVersion = null;

        // Action && Check
        assertThatCode(() -> versionHelper.checkVersion(expectedVersion, currentVersion))
                .doesNotThrowAnyException();
    }
}
