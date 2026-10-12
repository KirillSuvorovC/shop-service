package org.web.shop_service.user.support;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.web.shop_service.role.Role;
import org.web.shop_service.user.AuthUser;
import org.web.shop_service.user.User;

import java.util.List;

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
     * @return созданного пользователя с ролью ROLE_ADMIN типа {@link AuthUser}
     */
    public static AuthUser defaultAuthAdmin() {
        return new AuthUser(1L, "admin",
                List.of(new SimpleGrantedAuthority(Role.ROLE_ADMIN)));
    }
    /**
     * @return созданного пользователя с ролью ROLE_USER типа {@link AuthUser}
     */
    public static AuthUser defaultAuthUser() {
        return new AuthUser(1L, "user",
                List.of(new SimpleGrantedAuthority(Role.ROLE_USER)));
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
