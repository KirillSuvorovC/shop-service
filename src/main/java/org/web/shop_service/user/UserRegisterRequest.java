package org.web.shop_service.user;

public record UserRegisterRequest(String username, String password, String confirmPassword) {
}
