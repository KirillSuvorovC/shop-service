package org.web.shop_service.support;

import org.web.shop_service.user.CurrentUser;
import org.web.shop_service.user.support.TestUserFactory;
import org.web.shop_service.user.User;

public class TestUserDefaults {
    public static final User ENTITY_USER = TestUserFactory.defaultUserEntity();
    public static final User ENTITY_ADMIN = TestUserFactory.defaultAdminEntity();
    public static final CurrentUser CURRENT_USER = TestUserFactory.defaultAuthUser();
    public static final CurrentUser CURRENT_ADMIN = TestUserFactory.defaultAuthAdmin();
}
