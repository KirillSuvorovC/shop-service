package org.web.shop_service.user;

public class UserConstants {
    public static final int USERNAME_MIN_SIZE = 3;
    public static final int USERNAME_MAX_SIZE = 100;
    public static final String INVALID_USERNAME_SIZE = "username must be "
            + USERNAME_MIN_SIZE + '-' + USERNAME_MAX_SIZE +
            " characters long";
    public static final int PASSWORD_MIN_SIZE = 6;
    public static final int PASSWORD_MAX_SIZE = 100;
    public static final String INVALID_PASSWORD_SIZE = "password must be "
            + PASSWORD_MIN_SIZE + '-' + PASSWORD_MAX_SIZE +
            " characters long";
}
