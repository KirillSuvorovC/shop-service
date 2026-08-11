package org.web.shop_service.auth;

import org.web.shop_service.user.UserRegisterRequest;

public interface AuthService {
    JwtResponse createAuthToken(JwtRequest request);
    JwtResponse registerUser(UserRegisterRequest request);
}
