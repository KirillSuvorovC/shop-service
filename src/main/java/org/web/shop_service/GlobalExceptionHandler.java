package org.web.shop_service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.ValidationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.web.shop_service.auth.exceptions.UserAlreadyExistsException;
import org.web.shop_service.utils.ResponseError;
import org.web.shop_service.utils.exceptions.HttpHeaderIfMatchRequiredException;
import org.web.shop_service.utils.exceptions.InvalidRequestException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseError> handle(MethodArgumentNotValidException e) {
        final var errors = e.getBindingResult()
                            .getFieldErrors()
                            .stream()
                            .map(FieldError::getDefaultMessage)
                            .collect(Collectors.joining(", "));
        return ResponseError.asEntity(HttpStatus.BAD_REQUEST, errors);
    }

    @ExceptionHandler({ValidationException.class, InvalidRequestException.class})
    public ResponseEntity<ResponseError> badRequest(Exception e) {
        return ResponseError.asEntity(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler({EntityNotFoundException.class, UsernameNotFoundException.class})
    public ResponseEntity<ResponseError> notFound(Exception e) {
        return ResponseError.asEntity(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler({UserAlreadyExistsException.class, OptimisticLockingFailureException.class})
    public ResponseEntity<ResponseError> conflict(Exception e) {
        return ResponseError.asEntity(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<ResponseError> conflict() {
        return ResponseError.asEntity(HttpStatus.CONFLICT, "Data is old, reload page");
    }

    @ExceptionHandler(HttpHeaderIfMatchRequiredException.class)
    public ResponseEntity<ResponseError> preconditionFailed() {
        return ResponseError.asEntity(HttpStatus.PRECONDITION_FAILED,
                "HTTP-Header If-Match is required");
    }
}
