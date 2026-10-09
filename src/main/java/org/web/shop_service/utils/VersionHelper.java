package org.web.shop_service.utils;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class VersionHelper {
    /**
     * Сравнивает две версии через {@link Objects#equals(Object, Object)}.
     * Если не совпадают, то бросает исключение.
     * @throws OptimisticLockingFailureException если версии не совпадают
     * @param expectedVersion ожидаемая версия
     * @param currentVersion актуальная версия
     */
    public void checkVersion(Long expectedVersion, Long currentVersion) {
        if(!Objects.equals(expectedVersion, currentVersion)) {
            throw new OptimisticLockingFailureException("Data is old, reload page");
        }
    }
}
