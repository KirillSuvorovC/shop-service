package org.web.shop_service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CurrentUserProvider {
    private final UserService userService;

    @Transactional(readOnly = true)
    public CurrentUser getByUsername(String username) {
        User user = userService.getUserByUsername(username);
        return new CurrentUser(
                user.getId(),
                user.getUsername(),
                List.of(new SimpleGrantedAuthority(user.getRole().getName()))
        );
    }
}
