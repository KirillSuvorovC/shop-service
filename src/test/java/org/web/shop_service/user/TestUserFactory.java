package org.web.shop_service.user;

import org.web.shop_service.role.Role;

public class TestUserFactory {
    /**
     * @return созданного пользователя с ролью ROLE_USER типа {@link User}
     */
    public static User defaultUserEntity() {
        return create(Role.ROLE_USER);
    }
    /**
     * @return созданного пользователя с ролью ROLE_ADMIN типа {@link User}
     */
    public static User defaultAdminEntity() {
        return create(Role.ROLE_ADMIN);
    }
    /**
     * @return созданного пользователя с ролью ROLE_ADMIN типа {@link CurrentUser}
     */
    public static CurrentUser defaultAuthAdmin() {
        return new CurrentUser(defaultAdminEntity());
    }
    /**
     * @return созданного пользователя с ролью ROLE_USER типа {@link CurrentUser}
     */
    public static CurrentUser defaultAuthUser() {
        return new CurrentUser(defaultUserEntity());
    }

    private static User create(String roleName) {
        final var user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRole(new Role(roleName));
        return user;
    }
}
