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

    /**
     * Сравнивает две версии. Если не совпадают, то бросает исключение.
     * @deprecated использует сравнение объектов через оператор !=,
     *             из за чего при сравнении 1000L != 1000L бросит исключение.
     *             Вместо этого использовать метод {@link #checkVersion(Long, Long)}
     * @throws OptimisticLockingFailureException если версии не совпадают
     * @param expectedVersion ожидаемая версия
     * @param currentVersion актуальная версия
     */
    @Deprecated
    public static void check(Long expectedVersion, Long currentVersion) {
        if(expectedVersion != currentVersion) {
            throw new OptimisticLockingFailureException("Data is old, reload page");
        }
    }
}
