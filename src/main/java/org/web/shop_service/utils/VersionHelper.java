package org.web.shop_service.utils;

import org.springframework.dao.OptimisticLockingFailureException;

public class VersionHelper {
    public static void check(Long expectedVersion, Long currentVersion) {
        if(expectedVersion != currentVersion) {
            throw new OptimisticLockingFailureException("Data is old, reload page");
        }
    }
}
