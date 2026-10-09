package org.web.shop_service.auth;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.web.shop_service.auth.exceptions.UserAlreadyExistsException;
import org.web.shop_service.user.User;
import org.web.shop_service.user.UserRegisterRequest;
import org.web.shop_service.user.UserService;
import org.web.shop_service.utils.JwtUtils;
import org.web.shop_service.utils.exceptions.InvalidRequestException;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {
    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @Override
    public JwtResponse createAuthToken(JwtRequest request) {
        try {
            authenticationManager.authenticate(newAuthenticationToken(request));
        } catch (BadCredentialsException e) {
            throw new ValidationException("invalid login or password");
        }
        final var userDetails = userService.loadUserByUsername(request.username());
        final String token = jwtUtils.generateToken(userDetails);
        return new JwtResponse(token);
    }

    @Override
    public JwtResponse registerUser(UserRegisterRequest request) {
        if(!StringUtils.equals(request.password(), request.confirmPassword())) {
            throw new InvalidRequestException("passwords do not match");
        }
        final Optional<User> user = userService.findByUsername(request.username());
        if(user.isPresent()) {
            throw new UserAlreadyExistsException(request.username());
        }
        final var userDetails = userService.createUser(request);
        final String token = jwtUtils.generateToken(userDetails);
        return new JwtResponse(token);
    }

    private static UsernamePasswordAuthenticationToken newAuthenticationToken(
                                                        JwtRequest request) {
        return new UsernamePasswordAuthenticationToken(
                request.username(), request.password());
    }
}
