package org.web.shop_service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotNull;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.web.shop_service.auth.JwtRequest;
import org.web.shop_service.auth.exceptions.UserAlreadyExistsException;
import org.web.shop_service.utils.exceptions.HttpHeaderIfMatchRequiredException;
import org.web.shop_service.utils.exceptions.InvalidRequestException;

/**
 * Используется для тестирования обработки исключений
 * классом {@link GlobalExceptionHandler}
 */
@RestController
@Profile("test")
@RequestMapping("/test/exceptions")
public class TestExceptionController {

    // Исключения со статусом 400

    @PostMapping("/400/method-argument-not-valid-exception")
    public void handle(@Valid @RequestBody JwtRequest request) {}

    @PostMapping("/400/validation-exception")
    public void validationException() { throw new ValidationException(); }

    @PostMapping("/400/invalid-request-exception")
    public void invalidRequestException() { throw new InvalidRequestException(""); }

    // Исключения со статусом 404

    @PostMapping("/404/username-not-found-exception")
    public void usernameNotFoundException() { throw new UsernameNotFoundException(""); }

    @PostMapping("/404/entity-not-found-exception")
    public void entityNotFoundException() { throw new EntityNotFoundException(); }

    // Исключения со статусом 409

    @PostMapping("/409/user-already-exists-exception")
    public void userAlreadyExistsException() { throw new UserAlreadyExistsException(""); }

    @PostMapping("/409/optimistic-locking-failure-exception")
    public void optimisticLockingFailureException() { throw new OptimisticLockingFailureException(""); }

    @PostMapping("/409/optimistic-lock-exception")
    public void optimisticLockException() { throw new OptimisticLockException(""); }

    // Исключения со статусом 412

    @PostMapping("/412/http-header-if-match-required-exception")
    public void httpHeaderIfMatchRequiredException() { throw new HttpHeaderIfMatchRequiredException(); }
}
