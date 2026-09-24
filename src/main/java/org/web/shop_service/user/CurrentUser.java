package org.web.shop_service.user;

public record CurrentUser(Long id, String username) {
    public CurrentUser(User user) {
        this(user.getId(), user.getUsername());
    }
}
